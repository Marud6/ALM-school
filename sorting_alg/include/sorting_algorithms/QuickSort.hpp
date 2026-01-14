#pragma once
#include <vector>
#include <cstddef>
#include <utility>
#include <atomic>

extern std::atomic<bool> terminating;

template <typename T>
class QuickSort {
private:
    static size_t partition(std::vector<T>& arr, size_t low, size_t high) {
        T pivot = arr[high];
        size_t i = low;

        for (size_t j = low; j < high; ++j) {
            if (arr[j] < pivot) {
                std::swap(arr[i], arr[j]);
                ++i;
            }
        }
        std::swap(arr[i], arr[high]);
        return i;
    }

    static void quickSort(std::vector<T>& arr, size_t low, size_t high) {
        if (terminating.load(std::memory_order_relaxed))
            return;
        if (low < high) {
            size_t pi = partition(arr, low, high);
            if (pi > 0) quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

public:
    static void sort(std::vector<T>& arr) {
        if (!arr.empty())
            quickSort(arr, 0, arr.size() - 1);
    }
};
