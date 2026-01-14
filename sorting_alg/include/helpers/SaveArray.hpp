#include <vector>
#include <memory>
#include <fstream>
#include <string>
#include <iostream>

template <typename T>
std::unique_ptr<std::vector<T>> currentArray;


template <typename T>
void saveArray(const std::string& filename)
{
    if (!currentArray<T> || currentArray<T>->empty())
        return;

    std::ofstream out(filename, std::ios::out | std::ios::trunc);
    if (!out) {
        std::cerr << "Failed to open " << filename << "\n";
        return;
    }

    for (const T& x : *currentArray<T>) {
        out << x << '\n';
    }

    out.close();
}

