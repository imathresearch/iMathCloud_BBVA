#include <iostream>
#include <vector>
#include "imathcloud.h"

int main(void)
{
		std::cout << "size of char: " << sizeof(char) << "\n";
        std::cout << "Testing imathcloud dll:" << "\n";
		std::cout << "-----------------------" << "\n\n";
		
		std::cout << "Getting job status -1: Expected a json string" << "\n";
		std::cout << imath_getJSONJobStatus(22) << "\n\n";

		std::cout << "Getting numerical status code" << "\n";
		std::cout << imath_getJobStatus(22) << "\n\n";

		std::cout << "Getting percentages " << "\n";
		/*
		std::vector<double> pcts = imath_getJobPercentages(22);
		for (int i =0; i < pcts.size(); ++i) {
			std::cout << "Elem " << i << ": " << pcts[i] << "\n";
		}*/
		const char *p = imath_getJobPercentages(22);
		std::cout << p << "\n";
		imath_free((char *)p);
		
		std::string listFiles("test.csv,test1.csv,test2.csv");
		std::cout << "\n\n" << "Now,updating the files test.csv, test1.csv, test2.csv\n The result is a RAW JSON\n";
		p = imath_uploadFilesJSONState(listFiles.c_str());
		std::string filesOut((char *)p);
		std::cout << filesOut << "\n";
		std::cout << "The numerical status codes" << "\n";
		std::cout << imath_getFilesStatusFromJSON(p) << "\n";
		std::cout << "The ids of the updated files" << "\n";
		const char *ids = imath_getFilesIdFromJSON(p);
		std::cout << ids << "\n";
		imath_free((char *)p);
		
		std::cout << "\n\n" << "Now,starting plugin job for butler functionality\n The result is a RAW JSON\n";
		p = imath_executePlugin(ids);
		std::cout << p << "\n";
		
		long jobId = imath_getJobIdFromJSON(p);
		long jobStatus = imath_getJobStatusFromJSON(p);
		
		std::cout << "JobId: " << jobId << "\n" << "JobStatus: " << jobStatus << "\n";
		imath_free((char *)p);
		bool ok = true;
		if (imath_getJobStatus(jobId)!=0) {
			if (imath_getJobStatus(jobId)!=0) {
				if (imath_getJobStatus(jobId)!=0) {
					std::cout << "Not possible to download files. Its taking to much\n";
					ok=false;
				} 
			}
		}
		if (ok) {
			std::cout << "Starting downloading th results: " << jobId << ".zip\n";
			long res = imath_downloadFilesByJobId(jobId);
			std::cout << "Result: " << res << "\n";
		}
		return 0;
}