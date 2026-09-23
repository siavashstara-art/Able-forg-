package com.example

import com.example.core.network.NetworkConnectivityMonitor
import com.example.core.voice.SttTargetField
import com.example.ui.components.OnlineSyncStatus
import com.example.ui.components.TrafficSignalType
import org.junit.Assert.*
import org.junit.Test

class CoreStep3UnitTest {

  @Test
  fun testOnlineSyncStatusIntegrity() {
    val statuses = OnlineSyncStatus.values()
    assertEquals(4, statuses.size)

    // Verify non-empty labels and associated traffic signals for accessibility
    statuses.forEach { status ->
      assertTrue(status.faLabel.isNotBlank())
      assertTrue(status.enLabel.isNotBlank())
      assertNotNull(status.signal)
    }

    // Verify Pending status has MODIFIED signal (amber/pending), not READY (green) to avoid fake success
    assertEquals(TrafficSignalType.MODIFIED, OnlineSyncStatus.PENDING.signal)
    assertEquals(TrafficSignalType.READY, OnlineSyncStatus.SYNCED.signal)
    assertEquals(TrafficSignalType.ISSUE, OnlineSyncStatus.FAILED.signal)
  }

  @Test
  fun testSttTargetFieldsCoverage() {
    val fields = SttTargetField.values()
    assertEquals(5, fields.size)

    val expectedTargets = listOf("QUESTION", "ERROR", "COMMAND", "TEXT", "SEARCH")
    expectedTargets.forEach { name ->
      assertNotNull(fields.find { it.name == name })
    }
  }
}
