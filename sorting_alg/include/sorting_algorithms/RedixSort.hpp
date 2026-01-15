#pragma once

#include <vector>
#include <string>
#include <algorithm>
#include <type_traits>
#include <atomic>
#include <cstdint>
#include <cstring>

extern std::atomic<bool> terminating;


namespace RadixSort{

    // INTEGRAL TYPES
    template <typename T>
    static void radixSortIntegral(std::vector<T>& arr) {
        if (arr.empty()) return;

        std::vector<T> neg, pos;
        for (T v : arr)
            (v < 0 ? neg : pos).push_back(v);

        auto sortAbs = [&](std::vector<T>& v) {
            T maxVal = *std::max_element(v.begin(), v.end(),
                [](T a, T b) { return std::llabs((long long)a) < std::llabs((long long)b); });

            for (uint64_t exp = 1;
                 std::llabs((long long)maxVal) / exp > 0;
                 exp *= 10)
            {
                if (terminating.load(std::memory_order_relaxed)) return;

                size_t count[10] = {0};
                std::vector<T> output(v.size());

                for (T n : v)
                    count[(std::llabs((long long)n) / exp) % 10]++;

                for (int i = 1; i < 10; ++i)
                    count[i] += count[i - 1];

                for (int i = (int)v.size() - 1; i >= 0; --i) {
                    size_t digit = (std::llabs((long long)v[i]) / exp) % 10;
                    output[--count[digit]] = v[i];
                }

                v.swap(output);
            }
        };

        if (!neg.empty()) sortAbs(neg);
        if (!pos.empty()) sortAbs(pos);

        std::reverse(neg.begin(), neg.end());

        arr.clear();
        arr.insert(arr.end(), neg.begin(), neg.end());
        arr.insert(arr.end(), pos.begin(), pos.end());
    }

    // FLOATING POINT TYPES
    template <typename T>
    static void radixSortFloat(std::vector<T>& arr) {
        if (arr.empty()) return;

        using UInt = std::conditional_t<sizeof(T) == 4, uint32_t, uint64_t>;
        constexpr int bits = sizeof(UInt) * 8;

        std::vector<UInt> data(arr.size());

        for (size_t i = 0; i < arr.size(); ++i) {
            std::memcpy(&data[i], &arr[i], sizeof(T));
            data[i] ^= (data[i] >> (bits - 1)) ? ~UInt(0) : (UInt(1) << (bits - 1));
        }

        for (size_t byte = 0; byte < sizeof(UInt); ++byte) {
            if (terminating.load(std::memory_order_relaxed)) return;

            size_t count[256] = {0};
            std::vector<UInt> output(data.size());

            for (UInt v : data)
                count[(v >> (byte * 8)) & 0xFF]++;

            for (int i = 1; i < 256; ++i)
                count[i] += count[i - 1];

            for (int i = (int)data.size() - 1; i >= 0; --i) {
                size_t b = (data[i] >> (byte * 8)) & 0xFF;
                output[--count[b]] = data[i];
            }

            data.swap(output);
        }

        for (size_t i = 0; i < arr.size(); ++i) {
            data[i] ^= (data[i] >> (bits - 1)) ? ~UInt(0) : (UInt(1) << (bits - 1));
            std::memcpy(&arr[i], &data[i], sizeof(T));
        }
    }

    // STRINGS
    static void radixSortString(std::vector<std::string>& arr) {
        if (arr.empty()) return;

        size_t maxLen = 0;
        for (const auto& s : arr)
            maxLen = std::max(maxLen, s.size());

        for (int pos = (int)maxLen - 1; pos >= 0; --pos) {
            if (terminating.load(std::memory_order_relaxed)) return;

            size_t count[256] = {0};
            std::vector<std::string> output(arr.size());

            for (const auto& s : arr) {
                unsigned char c = pos < (int)s.size() ? s[pos] : 0;
                count[c]++;
            }

            for (int i = 1; i < 256; ++i)
                count[i] += count[i - 1];

            for (int i = (int)arr.size() - 1; i >= 0; --i) {
                unsigned char c = pos < (int)arr[i].size() ? arr[i][pos] : 0;
                output[--count[c]] = arr[i];
            }

            arr.swap(output);
        }
    }


    template <typename T>
    void sort(std::vector<T>& arr) {
        if constexpr (std::is_integral_v<T>) {
            radixSortIntegral(arr);
        }
        else if constexpr (std::is_floating_point_v<T>) {
            radixSortFloat(arr);
        }
        else if constexpr (std::is_same_v<T, std::string>) {
            radixSortString(arr);
        }
        else {
            static_assert(!sizeof(T), "Radix sort not supported for this type");
        }
    }

}