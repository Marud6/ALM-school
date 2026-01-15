#include <vector>
#include "helpers/FileLoader.hpp"

template <typename T>
double progress(const std::vector<T>& checkpoint, const std::vector<T>& finishedData) {
    std::size_t size = std::min(checkpoint.size(), finishedData.size());
    if (size == 0) return 0.0;
    std::size_t done = 0;
    for (std::size_t i = 0; i < size; ++i) {
        if (finishedData[i] == checkpoint[i]) done++;
    }
    return static_cast<int>((done * 100.0) / finishedData.size());
}


double DoneProgress(std::string alg, std::string file, int type) {
    if (type == 1) {
        auto checkpoint = loadFromFile<int>("./output/not_finished/checkpoint-"+alg+"-"+file);
        auto finishedData = loadFromFile<int>("./output/finished/finished-"+file);
        return progress<int>(checkpoint,finishedData);

    }
    if (type == 2) {
        auto checkpoint = loadFromFile<std::string>("./output/not_finished/checkpoint-"+alg+"-"+file);
        auto finishedData = loadFromFile<std::string>("./output/finished/finished-"+file);
        return progress<std::string>(checkpoint,finishedData);
    }
    if (type == 3) {
        auto checkpoint = loadFromFile<float>("./output/not_finished/checkpoint-"+alg+"-"+file);
        auto finishedData = loadFromFile<float>("./output/finished/finished-"+file);
        return progress<float>(checkpoint,finishedData);
    }
    return 0;
}
