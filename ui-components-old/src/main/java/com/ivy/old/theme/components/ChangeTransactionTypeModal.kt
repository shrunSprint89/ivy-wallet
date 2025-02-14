package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.util.IvyPreview
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalSet
import com.ivy.wallet.ui.theme.modal.ModalTitle
import java.util.*

@Composable
fun BoxWithConstraintsScope.ChangeTransactionTypeModal(
    title: String = stringResource(R.string.set_transaction_type),
    visible: Boolean,
    includeTransferType: Boolean,
    initialType: TrnTypeOld,
    id: UUID = UUID.randomUUID(),
    dismiss: () -> Unit,
    onTransactionTypeChanged: (TrnTypeOld) -> Unit
) {
    var transactionType by remember(id) {
        mutableStateOf(initialType)
    }

    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSet {
                save(
                    transactionType = transactionType,
                    onTransactionTypeChanged = onTransactionTypeChanged,
                    dismiss = dismiss,
                )
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = title)

        Spacer(Modifier.height(32.dp))

        TransactionTypeButton(
            transactionType = TrnTypeOld.INCOME,
            selected = transactionType == TrnTypeOld.INCOME,
            selectedGradient = GradientGreen,
            textSelectedColor = White
        ) {
            transactionType = TrnTypeOld.INCOME
            save(
                transactionType = transactionType,
                onTransactionTypeChanged = onTransactionTypeChanged,
                dismiss = dismiss,
            )
        }

        Spacer(Modifier.height(12.dp))

        TransactionTypeButton(
            transactionType = TrnTypeOld.EXPENSE,
            selected = transactionType == TrnTypeOld.EXPENSE,
            selectedGradient = Gradient(UI.colorsInverted.pure, UI.colors.neutral),
            textSelectedColor = UI.colors.pure
        ) {
            transactionType = TrnTypeOld.EXPENSE
            save(
                transactionType = transactionType,
                onTransactionTypeChanged = onTransactionTypeChanged,
                dismiss = dismiss,
            )
        }

        if (includeTransferType) {
            Spacer(Modifier.height(12.dp))

            TransactionTypeButton(
                transactionType = TrnTypeOld.TRANSFER,
                selected = transactionType == TrnTypeOld.TRANSFER,
                selectedGradient = GradientIvy,
                textSelectedColor = White
            ) {
                transactionType = TrnTypeOld.TRANSFER
                save(
                    transactionType = transactionType,
                    onTransactionTypeChanged = onTransactionTypeChanged,
                    dismiss = dismiss,
                )
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

private fun save(
    transactionType: TrnTypeOld,
    onTransactionTypeChanged: (TrnTypeOld) -> Unit,
    dismiss: () -> Unit
) {
    onTransactionTypeChanged(transactionType)
    dismiss()
}

@Composable
private fun TransactionTypeButton(
    transactionType: TrnTypeOld,
    selected: Boolean,
    selectedGradient: Gradient,
    textSelectedColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.squared)
            .background(
                brush = if (selected) selectedGradient.asHorizontalBrush() else SolidColor(UI.colors.medium),
                shape = UI.shapes.squared
            )
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp)
            .testTag("modal_type_${transactionType.name}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        val textColor = if (selected) textSelectedColor else UI.colorsInverted.pure

        IvyIcon(
            icon = when (transactionType) {
                TrnTypeOld.INCOME -> R.drawable.ic_income
                TrnTypeOld.EXPENSE -> R.drawable.ic_expense
                TrnTypeOld.TRANSFER -> R.drawable.ic_transfer
            },
            tint = textColor
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = when (transactionType) {
                TrnTypeOld.INCOME -> stringResource(R.string.income)
                TrnTypeOld.EXPENSE -> stringResource(R.string.expense)
                TrnTypeOld.TRANSFER -> stringResource(R.string.transfer)
            },
            style = UI.typo.b1.style(
                color = textColor
            )
        )

        if (selected) {
            Spacer(Modifier.weight(1f))

            IvyIcon(
                icon = R.drawable.ic_check,
                tint = textSelectedColor
            )

            Text(
                text = stringResource(R.string.selected),
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.SemiBold,
                    color = textSelectedColor
                )
            )

            Spacer(Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    IvyPreview {
        ChangeTransactionTypeModal(
            includeTransferType = true,
            visible = true,
            initialType = TrnTypeOld.INCOME,
            dismiss = {}
        ) {

        }
    }
}