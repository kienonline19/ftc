# Algorithm Problem Set

Five problems, from easy to moderately difficult. Read from standard input, write to standard output.

| # | Problem | Topic | Level |
|---|---------|-------|-------|
| 1 | Run-Length Compression | String processing | Easy |
| 2 | Digit Sum Extremes | Numbers / greedy | Easy–Medium |
| 3 | Merge Intervals | Sorting | Medium |
| 4 | Book Allocation | Binary search | Medium |
| 5 | Decode Nested String | Recursion | Medium–Hard |

---

## Problem 1 — Run-Length Compression

Replace each run of the same character with the character followed by the run length (always write the count, even 1). If the result is **not strictly shorter** than the original, print the original.

**Input:** one string `s` of English letters (case-sensitive), 1 ≤ |s| ≤ 10^5.

**Output:** the compressed string, or `s` unchanged.

| Input | Output | Note |
|-------|--------|------|
| `aabcccccaaa` | `a2b1c5a3` | 8 < 11, compress |
| `abc` | `abc` | `a1b1c1` is longer |
| `aabb` | `aabb` | Same length → keep original |
| `zzzzzzzzzzy` | `z10y1` | Two-digit count |

**Edge cases:** single character; equal length (keep original); counts ≥ 10.

---

## Problem 2 — Digit Sum Extremes

Find the **smallest** and **largest** numbers that have exactly `m` digits and digit sum `s`. No leading zeros (the number 0 counts as 1 digit).

**Input:** two integers `m s` (1 ≤ m ≤ 100, 0 ≤ s ≤ 900).

**Output:** `smallest largest`, or `-1 -1` if impossible.

| Input | Output | Note |
|-------|--------|------|
| `2 15` | `69 96` | Basic case |
| `5 20` | `10199 99200` | |
| `1 0` | `0 0` | 0 is a valid 1-digit number |
| `3 0` | `-1 -1` | Would need leading zeros |
| `2 19` | `-1 -1` | s > 9·m |

**Edge cases:** s = 0; s > 9·m; m = 100 (answer has 100 digits — build it as a string).

---

## Problem 3 — Merge Intervals

Given `n` closed intervals `[l, r]` in any order, merge all overlapping ones and print them sorted by start. Intervals that touch (e.g. `[1,4]` and `[4,5]`) **do** merge.

**Input:** `n`, then `n` lines `l r` (1 ≤ n ≤ 10^5, −10^9 ≤ l ≤ r ≤ 10^9).

**Output:** `k` (number of merged intervals), then `k` lines `l r`.

| Input intervals | Output | Note |
|-----------------|--------|------|
| `[1,3] [2,6] [8,10] [15,18]` | `3` / `1 6` / `8 10` / `15 18` | Basic |
| `[1,4] [4,5]` | `1` / `1 5` | Touching merges |
| `[1,2] [3,4]` | `2` / `1 2` / `3 4` | Gap → no merge |
| `[6,8] [1,9] [2,4]` | `1` / `1 9` | Unsorted, nested |

(`/` separates output lines.)

**Edge cases:** unsorted input; one interval inside another; touching endpoints; negative values.

---

## Problem 4 — Book Allocation

`n` books stand in a fixed order; book i has `a[i]` pages. Split them into exactly `k` contiguous, non-empty groups (one per student). Minimize the **largest** number of pages any student gets.

**Input:** `n k`, then `n` integers `a[i]` (1 ≤ k ≤ n ≤ 10^5, 1 ≤ a[i] ≤ 10^9).

**Output:** the minimum possible maximum.

| Input | Output | Note |
|-------|--------|------|
| `4 2` / `12 34 67 90` | `113` | (12 34 67) (90) |
| `5 3` / `10 20 30 40 50` | `60` | (10 20 30) (40) (50) |
| `3 1` / `5 1 7` | `13` | k = 1 → total |
| `3 2` / `1000000000 1000000000 1000000000` | `2000000000` | Needs 64-bit |

**Edge cases:** k = 1 (answer = sum); k = n (answer = max book); sums above 2^31 − 1; books must **not** be reordered.

---

## Problem 5 — Decode Nested String

`k[body]` means `body` repeated `k` times. Brackets can be nested. Letters outside brackets are copied as-is.

**Input:** encoded string `s` (1 ≤ |s| ≤ 1000, always valid, 1 ≤ k ≤ 1000, decoded length ≤ 10^6).

**Output:** the decoded string.

| Input | Output | Note |
|-------|--------|------|
| `3[a]2[bc]` | `aaabcbc` | Sequential groups |
| `3[a2[c]]` | `accaccacc` | Nested |
| `2[abc]3[cd]ef` | `abcabccdcdcdef` | Text after groups |
| `10[a]` | `aaaaaaaaaa` | Multi-digit count |
| `2[]x` | `x` | Empty body |

**Edge cases:** multi-digit counts; empty brackets; no brackets at all; deep nesting.