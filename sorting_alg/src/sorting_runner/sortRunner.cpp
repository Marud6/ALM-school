#include <iostream>
#include <thread>
#include <chrono>
#include "helpers/SaveArray.hpp"
#include "helpers/AlgorithmsRunner.hpp"
#include "helpers/DoneProgress.hpp"

#include <atomic>
#include <csignal>

std::atomic<bool> terminating{false};
void termHandler(int) {
    terminating.store(true, std::memory_order_relaxed);
}

int main(int argc, char* argv[]) {
    std::signal(SIGTERM, termHandler);
     if (argc < 4) {
        std::cerr << "Usage: " << argv[0]
                  << " <file> <type> <algorithm>\n";
        return 1;
    }
    std::string file = argv[1];
    int type = std::stoi(argv[2]);      // 1=int, 2= string 3 = float
    std::string alg = argv[3];          // alg name
if (type == 1) {
	RunAlgorithms<int>(alg, file);
    if (terminating.load()) {
       	std::cerr << "terminating\n";
        saveArray<int>("./output/not_finished/checkpoint-"+alg+"-"+file.erase(0, 14));
		double progress=DoneProgress(alg,file,type);
   		std::cout << "On"+ file+" with "+ alg +" correctly done: " << progress << " \n";
        return 1;
    }
}
if (type == 2) {
	RunAlgorithms<std::string>(alg, file);
if (terminating.load()) {
        std::cerr << "terminating\n";
        saveArray<std::string>("./output/not_finished/checkpoint-"+alg+"-"+file.erase(0, 14));
		double progress=DoneProgress(alg,file,type);
   		std::cout << "On"+ file+" with "+ alg +" correctly done: " << progress << " \n";
        return 1;
    }

}
if (type == 3) {
	RunAlgorithms<float>(alg, file);
if (terminating.load()) {
        std::cerr << "terminating\n";
        saveArray<float>("./output/not_finished/checkpoint-"+alg+"-"+file.erase(0, 14));
		double progress=DoneProgress(alg,file,type);
   		std::cout << "On"+ file+" with "+ alg +" correctly done: " << progress << " \n";
        return 1;
    }
}


    return 0;
}
