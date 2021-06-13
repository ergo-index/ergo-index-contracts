package contracts

/**
 * Sell Token Tx: Sells a token that is tracked in the core state's R7 and creates a new UTXO that is
 * spendable by the fund (i.e., the core state UTXO)
 * <ul>
 *   <li>
 *     INPUTS(0): The core state UTXO
 *   </li>
 *   <li>
 *     INPUTS(1): The UTXO of a token that the fund owns (and is consequentially stored in R7)
 *   </li>
 *   <li>
 *     OUTPUTS(0): The core state UTXO with an updated R5 to reflect which investors' Ergs were
 *     moved back into the core state UTXO, as well as an updated R7 to reflect the fact that one less UTXO
 *     is now spendable by the fund.
 *   </li>
 * </ul>
 */
object SellTokenContract {
  def run() = {
  }
}
