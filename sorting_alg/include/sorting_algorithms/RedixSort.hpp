#pragma once
#include <vector>
#include <algorithm>
#include <type_traits>
#include <atomic>

extern std::atomic<bool> terminating;

template <typename T>
class RadixSort {
    static_assert(std::is_integral<T>::value,"RadixSort requires an integral type");

private:
    static T getMax(const std::vector<T>& arr) {
        return *std::max_element(arr.begin(), arr.end());
    }

    static void countingSort(std::vector<T>& arr, T exp) {
        if (terminating.load(std::memory_order_relaxed))
            return;
        std::vector<T> output(arr.size());
        int count[10] = {0};

        for (T num : arr)
            count[(num / exp) % 10]++;

        for (int i = 1; i < 10; ++i)
            count[i] += count[i - 1];

        for (int i = arr.size() - 1; i >= 0; --i) {
            int digit = (arr[i] / exp) % 10;
            output[--count[digit]] = arr[i];
        }

        arr = output;
    }

public:
    static void sort(std::vector<T>& arr) {
        T max = getMax(arr);

        for (T exp = 1; max / exp > 0; exp *= 10)
            countingSort(arr, exp);
    }
};
