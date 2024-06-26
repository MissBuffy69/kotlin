/*
 * Copyright 2022 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#ifndef CUSTOM_ALLOC_CPP_SINGLEOBJECTPAGE_HPP_
#define CUSTOM_ALLOC_CPP_SINGLEOBJECTPAGE_HPP_

#include <atomic>
#include <cstdint>
#include <vector>

#include "AnyPage.hpp"
#include "AtomicStack.hpp"
#include "ExtraObjectPage.hpp"
#include "GCStatistics.hpp"

namespace kotlin::alloc {

class SingleObjectPage;

// TODO add separate PageWithSizeTracking
template<>
class alignas(kPageAlignment) AnyPage<SingleObjectPage> : Pinned {
private:
    friend class AtomicStack<SingleObjectPage>;
    // Used for linking pages together in `pages` queue or in `unswept` queue.
    std::atomic<SingleObjectPage*> next_ = nullptr;

protected:
    // Intentionally non-virtual. `AnyPage` should not be used in any context other than base class clause.
    // Please use concrete implementations instead.
    ~AnyPage() = default;
};

class alignas(kPageAlignment) SingleObjectPage : public AnyPage<SingleObjectPage> {
public:
    using GCSweepScope = gc::GCHandle::GCSweepScope;

    static GCSweepScope currentGCSweepScope(gc::GCHandle& handle) noexcept { return handle.sweep(); }

    static SingleObjectPage* Create(uint64_t cellCount) noexcept;

    void Destroy() noexcept;

    uint8_t* Data() noexcept;

    uint8_t* Allocate(size_t objectSizeBytes) noexcept;

    bool SweepAndDestroy(GCSweepScope& sweepHandle, FinalizerQueue& finalizerQueue) noexcept;

private:
    friend class Heap;

    explicit SingleObjectPage(size_t size) noexcept;

    [[nodiscard]] size_t objectSize() noexcept;
    [[nodiscard]] size_t pageSize() noexcept;

    // Testing method
    std::vector<uint8_t*> GetAllocatedBlocks() noexcept;

    struct alignas(8) {
        uint8_t data_[];
    };
};

} // namespace kotlin::alloc

#endif
