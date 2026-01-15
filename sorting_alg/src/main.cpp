#include <iostream>
#include <vector>
#include <chrono>
#include "helpers/FileLoader.hpp"
#include "helpers/DoneProgress.hpp"
#include <unistd.h>
#include <sys/wait.h>
#include <signal.h>
#include "sys/types.h"
#include "sys/sysinfo.h"
#include <thread>

#include <algorithm>
#include <fstream>

void runWithTimeout(const char* program, int timeoutSeconds, const  std::string file, std::string type, std::string alg) {//type int 1 string 2
    pid_t pid = fork();
    if (pid == 0) {
        execl(program, program, file.c_str(), type.c_str(), alg.c_str()  , nullptr);
        perror("failed");
        _exit(1);
    }
    else {
        int elapsed = 0;
        while (elapsed < timeoutSeconds) {
            int status;
            pid_t result = waitpid(pid, &status, WNOHANG);
            if (result == pid) {
                return;
            }
            std::this_thread::sleep_for(std::chrono::seconds(1));
            elapsed+=1;
        }
        std::cout << "Time limit reached! Killing process...\n";
        kill(pid, SIGTERM);
		std::this_thread::sleep_for(std::chrono::seconds(1));
        waitpid(pid, nullptr, 0);

		return;
    }
}
void setUp(std::string file){
 auto data = loadFromFile<std::string>("./data_source/"+file+".txt");
	   std::cerr << data.size() << std::endl;

    std::sort(data.begin(), data.end());
   std::ofstream outFile("./output/finished/finished-"+file+".txt");
    if (!outFile) {
        std::cerr << "Error opening file for writing!\n";
	return	;
    }
    for (const auto& value : data) {
        outFile << value << '\n';
    }
}

struct sysinfo memInfo;
int main() {
	std::vector<std::vector<std::string>> files= {
{"./data_source/rando_1M_cela_cisla.txt","1"},
{"./data_source/random_words_1M.txt","2"},
{"./data_source/random_10M_interval.txt","3"},
{"./data_source/random_integers_10M.txt","1"},
{"./data_source/random_words_10M.txt","2"}};

	std::vector<std::string> algs= {"selection","heap","quick","radix","bubble","insertion","merge",};
 for (auto& alg : algs) {
     	std::cout << "Algorithm:"<<alg <<"\n";

        for (auto& filePair : files) {

                const std::string& filePath = filePair[0];
                const std::string& type     = filePair[1];
                runWithTimeout("./sort_program", 3600, filePath, type, alg);
        }
    }
    return 0;
}
