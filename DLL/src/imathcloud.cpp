#include <stdio.h>
#include <string>
#include <sstream>
#include <iostream>
#include <fstream>
#include <vector>
#include <curl/curl.h>
#include <json/json.h>
#include "imathcloud.h"


/******************************
 * Internal functions
 ******************************/
const std::string HOST = "158.109.125.112";
const std::string PORT = "80";
const std::string PREFIX ="/com.iMathCloud/rest/beta/api" ;
const std::string CREDENTIALS_FILE = "credentials.imc";

struct WriteThis {
  const char *readptr;
  int sizeleft;
};

/**************************
 * A template function to split. Accepts multiple delimiters, is generic, uses native c++...
 * However, it is not efficient at all, so, do not use with long strings. t can be optimized using emplace_back 
 * instead of push_back, if using C++11.
 * 
 * usage: vector<std::string> v = split<std::string>("Hello, there; World", ";,");
 **************************/
template<typename T> 
std::vector<T> split(const T & str, const T & delimiters) {
    std::vector<T> v;
    typename T::size_type start = 0;
    auto int pos = str.find_first_of(delimiters, start);
    while(pos != T::npos) {
        if(pos != start) // ignore empty tokens
            v.push_back(str.substr(start, pos - start));
        start = pos + 1;
        pos = str.find_first_of(delimiters, start);
    }
    if(start < str.length()) // ignore trailing delimiter
        v.push_back(str.substr( start, str.length() - start)); // add what's left of the string
    return v;
}

/**
 * Allocates dynamic memory in the HEAP with the information contended in the *str
*/
char* get(const char *str)
{
   char* ret = (char *) malloc (sizeof(char) * (strlen(str)+1));
   strcpy(ret,str);
   return ret;
}

/************************
	Reads the credentials file CREDENTIALS_FILE and returns the string user:key 
 ************************/

 std::string getCredentialsString(){
	Json::Value root;
	Json::Reader reader;
	std::ifstream credentials (CREDENTIALS_FILE.c_str());
	std::string output("");
	
	if (credentials.is_open()) {
		bool parsedSuccess = reader.parse(credentials, root, false);
		if (parsedSuccess) {
			const Json::Value user = root["user"];
			const Json::Value key = root["key"];
			if (not (user.isNull() || key.isNull())) {
				output += user.asString();
				output += ":" + key.asString();
			}
		}
		credentials.close();
	} else {
		output += "butlerInt:butler2014";       // TODO: PROVISIONAL!
	}
	return output;
}

/***************
	Reconstruct the full url, including credentials
 ***************/
std::string getURL(std::string postfix) {
	std::string output("");
	std::string url("http://");
	std::stringstream strstream;
	
	std::string credentials = getCredentialsString();
	url += credentials + "@" + HOST + ":" + PORT + PREFIX + postfix;
	return url;
}


/******************************
 * cURL calls
 *******************************/

// The call back function used to capture the output of http requests
 static size_t writeCallback(void *contents, size_t size, size_t nmemb, void *userp) {
    ((std::string*)userp)->append((char*)contents, size * nmemb);
    return size * nmemb;
}

// The call back function used to write in files
size_t writeFileData(void *ptr, size_t size, size_t nmemb, FILE *stream) {
    size_t written;
    written = fwrite(ptr, size, nmemb, stream);
    return written;
}

 static size_t read_callback(void *ptr, size_t size, size_t nmemb, void *userp) {
	struct WriteThis *pooh = (struct WriteThis *)userp;

	if(size*nmemb > 0 && pooh->sizeleft > 0) {
		*(char *)ptr = pooh->readptr[0]; /* copy one single byte */ 
		pooh->readptr++;                 /* advance pointer */ 
		pooh->sizeleft--;                /* less data left */ 
		return 1;                        /* we return 1 byte at a time! */ 
	}
  return 0;                          /* no more data left to deliver */ 
}

// performs a simple get http request to imathcloud. 
std::string HTTPRequest(std::string postfix) {
	CURL *curl;
	CURLcode res;
	std::string output("");
	std::string url = getURL(postfix);
	curl = curl_easy_init();
	if(curl) {
		curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeCallback);
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &output);
 
		res = curl_easy_perform(curl);
		/* Check for errors */ 
		if(res != CURLE_OK)
			fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(res));
 
		/* always cleanup */ 
		curl_easy_cleanup(curl);
	}
	return output;
}

// performs a get call that downloads a file
long HTTPFileDownload(std::string fileName, std::string postfix) {
    CURL *curl;
    FILE *fp;
    CURLcode res;
    std::string url = getURL(postfix);
    curl = curl_easy_init();
    if (curl) {
        fp = fopen(fileName.c_str(),"wb");
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeFileData);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, fp);
        res = curl_easy_perform(curl);
        curl_easy_cleanup(curl);
        fclose(fp);
		return 0;
    }
    return -1;
}

// performs FORM POST for files upload
std::string HTTPFilesUpload(std::vector<std::string> files, std::string postfix) {
	CURL *curl;
	CURLcode res;
	std::string output("");
	std::string url = getURL(postfix);
	
	struct curl_httppost *formpost=NULL;
	struct curl_httppost *lastptr=NULL;
	//struct curl_slist *headerlist=NULL;
	//static const char buf[] = "Expect:";
	curl_global_init(CURL_GLOBAL_ALL);
	
	for (unsigned int i=0; i<files.size(); ++i) {
		curl_formadd(&formpost,
			&lastptr,
			CURLFORM_COPYNAME, "uploadedFile",
			CURLFORM_FILE, files[i].c_str(),
			CURLFORM_END);
	}
	curl = curl_easy_init();
	//headerlist = curl_slist_append(headerlist, buf);
	if(curl) {
		curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeCallback);
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &output);
		curl_easy_setopt(curl, CURLOPT_HTTPPOST, formpost);
		res = curl_easy_perform(curl);
		if(res != CURLE_OK)
			fprintf(stderr, "curl_easy_perform() failed: %s\n",
				curl_easy_strerror(res));
 
		curl_easy_cleanup(curl);
		curl_formfree(formpost);
	}
	return output;
}

std::string HTTPjsonPOST(std::string jsonStr, std::string postfix) {
	CURL *curl;
	CURLcode res;
	std::string output("");
	std::string url = getURL(postfix);
	
	struct curl_httppost *formpost=NULL;
	struct curl_httppost *lastptr=NULL;
	//static const char buf[] = "Expect:";
	struct curl_slist *headers = NULL;
    headers = curl_slist_append(headers, "Content-Type: application/json");
	curl_global_init(CURL_GLOBAL_ALL);
	
	struct WriteThis pooh;

	pooh.readptr = jsonStr.c_str();
	pooh.sizeleft = strlen(jsonStr.c_str());
	curl = curl_easy_init();
	//headerlist = curl_slist_append(headerlist, buf);
	if(curl) {
		std::cout << "easy_init performed\n"<< jsonStr.c_str() <<"\n" << url.c_str();
		curl_easy_setopt(curl, CURLOPT_POST, 1L);
		curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
		curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers); 
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeCallback);
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &output);
		curl_easy_setopt(curl, CURLOPT_READDATA, &pooh);
		curl_easy_setopt(curl, CURLOPT_READFUNCTION, read_callback);
		curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, pooh.sizeleft);
		//curl_easy_setopt(curl, CURLOPT_VERBOSE, 1L);
		res = curl_easy_perform(curl);
		if(res != CURLE_OK)
			fprintf(stderr, "curl_easy_perform() failed: %s\n",
				curl_easy_strerror(res));
		curl_easy_cleanup(curl);
	}
	return output;
}

std::string getJSONJobStatus(long x) {
	std::string postfix("/exec/");
	std::string jobId;
	std::stringstream strstream;
	strstream << x;		// Convert long to string
	strstream >> jobId;
	postfix += jobId;
	return HTTPRequest(postfix);
}


/******************************
	JSON formatted string parsers
 ******************************/
 
// Extracts the status numerical code from a json string 
int getStatusFromJSON(std::string jsonStr){
	Json::Value root;
	Json::Reader reader;
	bool parsedSuccess = reader.parse(jsonStr, root, false);
	int retValue = -100;
	if (parsedSuccess) {
		const Json::Value status = root["status"];
		if (not status.isNull()){
			const Json::Value code = status["code"];
			if (not code.isNull()) {
				retValue = code.asInt();
			}
		}
	}
	return retValue;
}

// Extracts the percentage values from a json string returning a vector<double>
std::vector<double> getPercentagesFromJSON(std::string jsonStr) {
	Json::Value root;
	Json::Reader reader;
	bool parsedSuccess = reader.parse(jsonStr, root, false);
	std::vector<double> retValue;
	
	if (parsedSuccess) {
		const Json::Value pcts = root["pcts"];
		if (not pcts.isNull()){
			std::vector<double> aux(pcts.size());
			for(unsigned int i=0; i<pcts.size(); ++i) 
			{ 
				std::stringstream ss(pcts[i].asString());
				ss >> aux[i];
			}
			retValue = aux;
		}
	}
	return retValue;
}

// The same as before returning a string with double values separated by comas.
std::string getPercentagesFromJSON_str(std::string jsonStr) {
	Json::Value root;
	Json::Reader reader;
	bool parsedSuccess = reader.parse(jsonStr, root, false);
	std::string retValue("");
	
	if (parsedSuccess) {
		const Json::Value pcts = root["pcts"];
		if (not pcts.isNull()){
			for(unsigned int i=0; i<pcts.size(); ++i) 
			{ 
				if (i>0) {
					retValue += ",";
				}
				retValue += pcts[i].asString();
			}
		}
	}
	return retValue;
}

std::string extractStatus(Json::Value elem) {
	Json::Value status = elem["status"];
	if (not status.isNull()) {
		Json::Value code = status["code"];
		if (not code.isNull()) {
			int codeInt = code.asInt();
			std::stringstream ss;
			ss << codeInt;
			std::string codeNum = ss.str();
			return codeNum;
		}			
	}
	return "";
}

std::string getResourcesStatusFromString(std::string jsonStr) {
	Json::Value root;
	Json::Reader reader;
	bool parsedSuccess = reader.parse(jsonStr, root, false);
	std::string retValue("");
	if (parsedSuccess) {
		if (not root.isNull()) {
			if (root.isArray()) {
				for(unsigned int i=0; i<root.size(); ++i) {
					std::string codeNum = extractStatus(root[i]);
					if (i>0) retValue += ",";
					retValue += codeNum;
				}
			} else {
				retValue = extractStatus(root);
			}
		}
	}
	return retValue;
}

std::string extractId(Json::Value elem) {
	Json::Value resource = elem["resource"];
	if (not resource.isNull()) {
		std::string idExtend = elem["resource"].asString();
		std::string id = idExtend.substr(5);	// Since resource is of the form data/{id}, or exec/{id} and we are only interested in the id
		return id;
	}
	return "";
}

std::string getResourcesIdFromString(std::string jsonStr) {
	Json::Value root;
	Json::Reader reader;
	bool parsedSuccess = reader.parse(jsonStr, root, false);
	std::string retValue("");
	if (parsedSuccess) {
		if (not root.isNull()) {
			if (root.isArray()) {	// If root is an array, we return the ids as comma separated values
				for(unsigned int i=0; i<root.size(); ++i) {
					std::string id = extractId(root[i]);
					if (i>0) retValue += ",";
					retValue += id;
				}
			} else {
				retValue = extractId(root);
			}
		}
	}
	return retValue;
}

std::string getFilesStatusFromString(std::string jsonStr) {
	return getResourcesStatusFromString(jsonStr);
}

std::string getFilesIdFromString(std::string jsonStr) {
	return getResourcesIdFromString(jsonStr);
}

/******************************
 * Exported DLL functions
 *******************************/

/**
 * Return the raw JSON received after checking the status of a job
 */
const char *imathcloud_dll imath_getJSONJobStatus(long x) {
	return get(getJSONJobStatus(x).c_str());
}

/**
 * Return the raw JSON received after uploading the files
 * We assume file names are separated by commas (,)
 */
const char * imathcloud_dll imath_uploadFilesJSONState(const char * filesStr) {
	std::string fileStrS(filesStr);
	std::vector<std::string> files = split<std::string>(fileStrS, ",");
	std::string postfix("/data/upload");
	return get(HTTPFilesUpload(files, postfix).c_str());
}

/**
 * Given the output of the the function imath_uploadFilesJSONState, it returns the ids of the updated data resources.
 * The results is a char * with ids separated by commas.
*/
const char * imathcloud_dll imath_getFilesIdFromJSON(const char *jsonStr) {
	std::string jsonString(jsonStr);
	std::string ret = getFilesIdFromString(jsonString);
	return get(ret.c_str());
}

/**
 * Executes the specific plugin job for Butler functionality (listpair-based).
 * Returns the JSON string defining the status of the job and its ids.
 * Accepts a list of filesIds (comma separated ids) to be used in the plugin, as returned 
 * in the method imath_getFilesIdFromJSON.
*/
const char * imathcloud_dll imath_executePlugin(const char *filesIds) {
	//{"dataFiles":[2], "execFiles":[], "params":[]}
	std::string fileIdsStr(filesIds);
	std::string jsonEntry("{\"dataFiles\":[");
	jsonEntry += fileIdsStr + "],\"execFiles\":[], \"params\":[]}";
	std::string postfix("/exec/plugin/listpair/butler");
	std::string ret = HTTPjsonPOST(jsonEntry, postfix);
	return get(ret.c_str());
}
/**
 * Given the output of imath_executePlugin it returns the numerical status of the resources
*/
 long imathcloud_dll imath_getJobStatusFromJSON(const char *jsonStr) {
 	std::string statusStr = getResourcesStatusFromString(jsonStr);
	std::stringstream ss(statusStr);
	long status;
	ss >> status;
	if (!ss.fail()) {
		return status;
	}
	return -100;
 }
 
/**
 * Given the output of the previous call, it returns the Id of the Job
*/
long imathcloud_dll imath_getJobIdFromJSON(const char *jsonStr) {
	std::string idStr = getResourcesIdFromString(jsonStr);
	std::stringstream ss(idStr);
	long id;
	ss >> id;
	if (!ss.fail()) {
		return id;
	}
	return -100;
}

long imathcloud_dll imath_downloadFilesByJobId(long jobId) {
	std::string postfix("/exec/result/");
	std::string jobIdStr;
	std::stringstream strstream;
	strstream << jobId;		// Convert long to string
	strstream >> jobIdStr;

	postfix += jobIdStr;
	std::string fileName("");
	fileName += jobIdStr;
	fileName += ".zip";
	return HTTPFileDownload(fileName, postfix);
}


/**
 * Given the output of the the function imath_uploadFilesJSONState, it returns the numerical status of the data resources.
 * The results is a char * with ids separated by commas.
*/
const char * imathcloud_dll imath_getFilesStatusFromJSON(const char *jsonStr) {
	std::string jsonString(jsonStr);
	std::string ret = getFilesStatusFromString(jsonString);
	return get(ret.c_str());
}

/**
 * Return the percentage of completion of a job
 */
const char * imathcloud_dll imath_getJobPercentages(long x) {
	std::string jsonStr= getJSONJobStatus(x);
	return get(getPercentagesFromJSON_str(jsonStr).c_str());
}
/**
 * Return the numerical status of the job
 */
int imathcloud_dll imath_getJobStatus(long x) {
	std::string jsonStr= getJSONJobStatus(x);
	return getStatusFromJSON(jsonStr);
}

/**
 * Method to free dynamically allocated memory when returning strings
 *
 */
 
void imathcloud_dll imath_free(char *p) {
	free(p);
}

