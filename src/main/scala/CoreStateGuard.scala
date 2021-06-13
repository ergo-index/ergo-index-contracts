/*
 Below is pseudocode for the guard for the core state UTXO.
 It is essentially a multi-conditional giving a specific guard that must be met depending on
 the type of transaction that the core state UTXO is being used as an input for.
 Note that Tx type will be determined by the guard script hashes held in R8.
 For example, we can determine that the Tx is a Buy Token Tx with the following pseudocode:
 val buyTokenTxGuardHash = OUTPUTS(0).R8.get("Buy Token")
 val isBuyTokenTx = buyTokenTxGuardHash == OUTPUTS(1).R1


val isValidBuyTokenTx = txType == BUY_TOKEN &&
                        proveDlog(ringSignatureOfAnyInvestor) &&
                        // INPUTS(0) and OUTPUTS(0) are the core state UTXOs before and after the Tx
                        INPUTS(0).R0 == OUTPUTS(0).R0 - MinTxFee - amtSpent
                        // Guard script can't be modified
                        INPUTS(0).R1 == OUTPUTS(0).R1
                        INPUTS(0).R2 == OUTPUTS(0).R2
                        INPUTS(0).R4 == OUTPUTS(0).R4
                        // R5 tracks how many nanoErgs each investor owns
                        INPUTS(0).R5 == OUTPUTS(0).R5 - investorsWhoseFundsWereUsed
                        // R7 tracks the tokens the fund is invested in
                        INPUTS(0).R6 == OUTPUTS(0).R6 + hashOfTokenPurchased
                        INPUTS(0).R8 == OUTPUTS(0).R8
                        // OUTPUTS(1) is our token and needs to be spendable by the core state UTXO
                        OUTPUTS(1).R0 == amtSpent
                        OUTPUTS(1).R1 == scriptAllowingCoreStateToSpendIt

val isValidUpdateSettingsTx = txType == UPDATE_SETTINGS &&
                              proveDlog(fundManager) &&
                              INPUTS(0).R0 == INPUTS(1).R0 - MinTxFee &&
                              INPUTS(0).R1 == INPUTS(1).R1 &&
                              INPUTS(0).R2 == INPUTS(1).R2 &&
                              INPUTS(0).R4 == INPUTS(1).R4 &&
                              INPUTS(0).R5 == INPUTS(1).R5 &&
                              // R6 contains the portfolio settings, so it is permitted to be modified
                              INPUTS(0).R7 == INPUTS(1).R7 &&
                              INPUTS(0).R8 == INPUTS(1).R8

val fundingScript = isValidBuyTokenTx || isValidSellTokenTx ||
                    isValidJoinInvestorTx || isValidLiquidateInvestorTx ||
                    isValidUpdateSettingsTx

*/