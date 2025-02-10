# SNHU CS-499 Capstone Portfolio Webpage Overview

# Nuoro Weight Management App

## Overview
The **Nuoro Weight Management App** is an Android-based mobile application designed to help users track their weight, set goals, and analyze progress through data visualization. Originally developed in **CS-360: Mobile Architect & Programming**, it has been enhanced as part of **CS-499: Computer Science Capstone**, focusing on **Software Engineering & Design (Enhancement One)**, **Algorithms & Data Structures (Enhancement Two)**, and **Databases (Enhancement Three)**.

---

## Features

### **Original Implementation**
- Basic SQLite storage for weight logs.
- Static text-based weight entry display.
- Simple goal-setting functionality.
- No data validation, sorting, or filtering.
- Limited user interactivity and feedback.

---

## **Enhancement One: Software Engineering & Design**
- **Input Validation**:
  - Ensured valid date format (`MM/DD/YY`) and weight range (`1-500 lbs`).
  - Prevented invalid entries from being stored.
- **Interactive Data Management**:
  - Replaced static text with a `RecyclerView` supporting edit and delete actions.
- **Data Visualization**:
  - Integrated **MPAndroidChart** for dynamic weight trend visualization.
- **Improved SMS Permissions**:
  - Fixed broken request flow, ensuring correct permission handling.
- **User Feedback Enhancements**:
  - Added error messages, confirmation dialogs, and success indicators.
- **Database Enhancements**:
  - Refactored `DatabaseHelper` to support real-time updates.
- **Goal Weight Management**:
  - Displayed goal weight on the graph for easy tracking.

---

## **Enhancement Two: Algorithms & Data Structures**
- **Sorting Options**:
  - Sort by **Date (Ascending/Descending)**.
  - Sort by **Weight (Ascending/Descending)**.
- **Search Functionality**:
  - Filter by **date range** and **weight range**.
- **Optimized Database Queries**:
  - Implemented `ORDER BY` for sorting and `WHERE` filters for searching.
- **Bug Fixes**:
  - Fixed chart disappearing issue when sorting descending.
  - Resolved RecyclerView scrolling issues.

---

## **Enhancement Three: Databases**
- **Error Handling**:
  - Implemented structured exception handling in CRUD methods to improve reliability and debugging.
- **Query Optimization**:
  - Added MongoDB indexes for commonly queried fields, such as `breed` and `location`, to enhance performance.
- **Secure Credential Storage**:
  - Replaced hardcoded credentials with environment variables for improved security.
- **Documentation**:
  - Enhanced inline comments and created a detailed guide for MongoDB queries and operations.
- **Unit Testing**:
  - Developed comprehensive unit tests to validate CRUD operations and ensure database reliability.

---

## **Screenshots**

### **Enhancement One: Software Engineering & Design**
| Login Screen | SMS Permission Request |
|-------------|-----------------------|
| ![Login](images/Login%20Page.png) | ![SMS Request](images/SMS%20Request.png) |

| Account Creation | Initial Data Display |
|----------------|---------------------|
| ![Create Account](images/Create%20Account%20Popup.png) | ![Initial Display](images/DataDisplayInitial.png) |

| Edit Entry Dialog | Delete Entry Confirmation |
|------------------|-------------------------|
| ![Edit Entry](images/Edit%20Entry.png) | ![Delete Entry](images/Delete%20Entry.png) |

| Goal Weight Entry | Clear All Data Confirmation |
|------------------|---------------------------|
| ![Goal Weight](images/Goal%20Weight.png) | ![Clear Data](images/Clear%20All%20Data.png) |

---

### **Enhancement Two: Algorithms & Data Structures**
| Sort by Date (Ascending) | Sort by Date (Descending) |
|--------------------------|--------------------------|
| ![Sort by Date A](images/Sort%20by%20Date%20A.png) | ![Sort by Date D](images/Sort%20by%20Date%20D.png) |

| Sort by Weight (Ascending) | Sort by Weight (Descending) |
|----------------------------|----------------------------|
| ![Sort by Weight A](images/Sort%20by%20Weight%20A.png) | ![Sort by Weight D](images/Sort%20by%20Weight%20D.png) |

| Search & List | Input Validation |
|--------------|-----------------|
| ![List](images/List.png) | ![Invalid Weight](images/Invalid%20Weight%20Dialog.png) |

---

### **Enhancement Three: Databases**
| Dashboard Filtering | Enhanced Dashboard Integration |
|----------------------|------------------------------|
| ![Dashboard Filtering](images/enhancement3/Dash_Filter.png) | ![Enhanced Dashboard](images/enhancement3/Dash_Modules.png) |

| MongoDB Indexes | Centralized Error Logging |
|-----------------|--------------------------|
| ![Indexes](images/enhancement3/Indexes.png) | ![Error Logging](images/enhancement3/Log.png) |

| Unit Tests for CRUD | Secure Credential Storage |
|--------------------|---------------------------|
| ![Unit Tests](images/enhancement3/Test_Crud.png) | ![Credentials](images/enhancement3/User_Pass.png) |

---

## **Technical Implementation**
- **Software Engineering and Design**:
  - Added validation, interactivity (RecyclerView), and visualizations (MPAndroidChart).
- **Algorithms & Data Structures**:
  - Enhanced sorting and filtering for user weight data, optimized query performance.
- **Databases**:
  - Added structured error handling, MongoDB indexing, and secure credential management.

---

## **How to Run**
1. Clone this repository.
2. Open in **Android Studio** for the Nuoro app or **Jupyter Notebook** for the Grazioso Salvare Dashboard.
3. Run the project on a compatible emulator or physical device.

---

## **Acknowledgments**
Developed as part of **CS-360 & CS-499 at SNHU**. Special thanks to course instructors for their guidance.
