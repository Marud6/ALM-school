#pragma once
#include <vector>
#include <atomic>

extern std::atomic<bool> terminating;

template <typename T>
class BubbleSort {
public:
    static void sort(std::vector<T>& arr) {
        size_t n = arr.size();
        bool swapped;
        do {
            swapped = false;
            for (size_t i = 0; i < n - 1; ++i) {
                if (terminating.load(std::memory_order_relaxed))
                    return;
                if (arr[i] > arr[i + 1]) {
                    std::swap(arr[i], arr[i + 1]);
                    swapped = true;
                }
            }
            --n;
        } while (swapped);
    }
};
