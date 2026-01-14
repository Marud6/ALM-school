#pragma once
#include <vector>
#include <atomic>

extern std::atomic<bool> terminating;


template <typename T>
class SelectionSort {
public:
    static void sort(std::vector<T>& arr) {
        size_t n = arr.size();
        for (size_t i = 0; i < n - 1; ++i) {
            size_t minIndex = i;
            for (size_t j = i + 1; j < n; ++j) {
                if (terminating.load(std::memory_order_relaxed))
                    return;
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            std::swap(arr[i], arr[minIndex]);
        }
    }
};
