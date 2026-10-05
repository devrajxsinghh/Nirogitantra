package com.example.telemedicine.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WelfareRepository {
    /** Replace with real API call; keep the signature. */
    fun loadDashboard(area: Area): Flow<WelfareSnapshot> = flow {
        delay(300) // mimic network
        emit(WelfareMock.snapshot(area))
    }
}
