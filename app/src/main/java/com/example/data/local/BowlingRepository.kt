package com.example.data.local

import com.example.data.model.BowlingDeliveryAnalysis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BowlingRepository(private val dao: BowlingDao) {
  val allAnalyses: Flow<List<BowlingDeliveryAnalysis>> = dao.getAllAnalyses().map { list ->
    list.map { it.toModel() }
  }

  suspend fun insertAnalysis(analysis: BowlingDeliveryAnalysis): Long {
    val entity = BowlingAnalysisEntity.fromModel(analysis)
    return dao.insertAnalysis(entity)
  }

  suspend fun getAnalysisById(id: Long): BowlingDeliveryAnalysis? {
    return dao.getAnalysisById(id)?.toModel()
  }

  suspend fun deleteAnalysis(analysis: BowlingDeliveryAnalysis) {
    dao.deleteAnalysis(BowlingAnalysisEntity.fromModel(analysis))
  }
}
