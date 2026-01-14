#pragma once

#include <vector>
#include <fstream>
#include <string>
#include <stdexcept>

template <typename T>
std::vector<T> loadFromFile(const std::string& filename) {
    std::vector<T> data;
    std::ifstream file(filename);

    if (!file.is_open()) {
        throw std::runtime_error("Could not open file: " + filename);
    }

    T value;
    while (file >> value) {
        data.push_back(value);
    }

    file.close();
    return data;
}
