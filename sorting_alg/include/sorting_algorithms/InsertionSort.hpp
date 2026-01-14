#pragma once
#include <vector>
#include <atomic>

extern std::atomic<bool> terminating;

template <typename T>
class InsertionSort {
public:
    static void sort(std::vector<T>& arr) {
        size_t n = arr.size();
        for (size_t i = 1; i < n; ++i) {
            T key = arr[i];
            size_t j = i;
            while (j > 0 && arr[j - 1] > key) {
                if (terminating.load(std::memory_order_relaxed))
                    return;
                arr[j] = arr[j - 1];
                --j;
            }
            arr[j] = key;
        }
    }
};