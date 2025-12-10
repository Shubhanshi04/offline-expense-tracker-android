package com.shubhanshi.smartexpense.data

import com.shubhanshi.smartexpense.data.local.TransactionEntity
import com.shubhanshi.smartexpense.domain.model.Transaction

fun TransactionEntity.toDomain(): Transaction =
    Transaction(
        id = id,
        title = title,
        amount = amount,
        dateEpochDay = dateEpochDay,
        type = type
    )

fun Transaction.toEntity(): TransactionEntity =
    TransactionEntity(
        id = id,
        title = title,
        amount = amount,
        dateEpochDay = dateEpochDay,
        type = type
    )
