# Cache Memory Simulator

A Java desktop application that simulates the behavior of a **CPU cache memory**, allowing users to visualize memory accesses, cache hits/misses, and the effect of different replacement policies.

The project demonstrates core **computer architecture concepts** such as cache organization, replacement strategies, and write policies through an interactive graphical interface.

---

# Features

- Simulation of **cache memory operations**
  - memory read (access)
  - memory write
  - cache block eviction
- Visualization of **cache state and main memory**
- Highlighting operations:
  - green HIT
  - red MISS
  - yellow WRITE
- Support for multiple **replacement policies**
  - FIFO (First In First Out)
  - LRU (Least Recently Used)
- **Write-Through policy** implementation
- Run simulations using **manual input or operation files**
- Performance statistics:
  - Hit count
  - Miss count
  - Hit rate
  - Miss rate

---

# Tech Stack

- **Java**
- **Java Swing** – graphical user interface
- **Java Collections Framework**
- **File I/O**

---

The architecture separates:

- **Model** – memory structures  
- **Simulation** – cache behavior logic  
- **GUI** – user interaction and visualization  

This modular design allows easy extension with new policies or mapping strategies.

---

# Implemented Concepts

- Fully Associative Cache
- FIFO Replacement Policy
- LRU Replacement Policy
- Write-Through Write Policy
- Cache Hit/Miss Tracking
- Cache Performance Metrics

---

# What This Project Demonstrates

- Understanding of **CPU cache mechanisms**
- Implementation of **replacement algorithms**
- **Object-Oriented Design in Java**
- Development of **interactive desktop applications**
- Designing **modular software architecture**
