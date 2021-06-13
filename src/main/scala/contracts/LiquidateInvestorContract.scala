package contracts

/**
 * Investor Liquidates Tx: Returns funds to an investor (up to the full value of his/her shares),
 * spending some of the fund's invested tokens if necessary
 * <ul>
 *   <li>
 *     INPUTS(0): The core state UTXO
 *   </li>
 *   <li>
 *     INPUTS(1)-INPUTS(N): The UTXOs of tokens that the fund owns (and are consequentially stored in R7).
 *     R7 must show that each token is at least partially owned by the investor.
 *   </li>
 *   <li>
 *     OUTPUTS(0): The core state UTXO with updated R5 and R7 to reflect the investor's updated shares, as well
 *     as the fund's new amount of tokens if the Tx needed to spend some. If the investor was only partially
 *     in some of the tokens, then the change will go back into the fund to be invested in more tokens in
 *     a future Buy Token Tx, and R5 will reflect this
 *   </li>
 *   <li>
 *     OUTPUTS(1): A UTXO with the value that the investor liquidated
 *   </li>
 * </ul>
 */
object LiquidateInvestorContract {
  def run() = {
  }
}
