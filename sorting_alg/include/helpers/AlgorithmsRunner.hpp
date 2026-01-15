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
    auto start = std::chrono::high_resolution_clock::now();
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
        RadixSort::sort(data);
    else {
        std::cerr << "Unknown algorithm\n";
        return 1;
    }
    auto end = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double, std::milli> duration = end - start;
    std::cout << "On"+ file+" with "+ alg +" Time: " << duration.count() << " ms\n";
    return 0;

}
