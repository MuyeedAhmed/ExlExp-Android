package com.muyeedahmed.exldroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exldroid.domain.model.AccountType

@Composable
fun AccountBadge(
    accountType: AccountType,
    modifier: Modifier = Modifier
) {
    val (label, bg, fg) = when (accountType) {
        AccountType.CHECKING -> Triple("CHECKING", Color(0xFFE0F2FE), Color(0xFF0369A1))
        AccountType.SAVINGS -> Triple("SAVINGS", Color(0xFFDCFCE7), Color(0xFF15803D))
        AccountType.BROKERAGE -> Triple("BROKERAGE", Color(0xFFF3E8FF), Color(0xFF7E22CE))
        AccountType.CREDIT_CARD -> Triple("CREDIT", Color(0xFFFEE2E2), Color(0xFFB91C1C))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}
