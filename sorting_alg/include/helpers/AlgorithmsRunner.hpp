#include <vector>
#include <fstream>
#include <string>
#include <stdexcept>
#include "helpers/FileLoader.hpp"
#include "sorting_algorithms/SelectionSort.hpp"
#include "sorting_algorithms/BubbleSort.hpp"
#include "sorting_algorithms/InsertionSort.hpp"
#include "sorting_algorithms/HeapSort.hpp"
#include "sorting_algorithms/MergeSort.hpp"
#include "sorting_algorithms/QuickSort.hpp"
#include "sorting_algorithms/RedixSort.hpp"

template <typename T>
int RunAlgorithms(std::string alg, std::string file) {
    currentArray<T> = std::make_unique<std::vector<T>>(loadFromFile<T>(file));
    auto& data = *currentArray<T>;
    if (alg == "selection")
        SelectionSort<T>::sort(data);
    else if (alg == "bubble")
        BubbleSort<T>::sort(data);
    else if (alg == "insertion")
        InsertionSort<T>::sort(data);
    else if (alg == "heap")
        HeapSort<T>::sort(data);
    else if (alg == "merge")
        MergeSort<T>::sort(data);
    else if (alg == "quick")
        QuickSort<T>::sort(data);
    else if (alg == "radix")
        if constexpr (std::is_same_v<T, int>) {
            RadixSort<T>::sort(data);
        } else {
            std::cerr << "RadixSort supported only for int\n";
            return 1;
        }
    else {
        std::cerr << "Unknown algorithm\n";
        return 1;
    }
    return 0;

}
