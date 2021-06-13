package contracts

/**
 * Buy Token Tx: creates a UTXO that can be used on the Ergo DEX to purchase an NFT or token. This
 * will hopefully be able to work with other blockchains in the future to provide native assets in addition
 * to wrapped assets (possibly by using THORchain, Gravity, and/or IBC protocol).
 * <ul>
 *   <li>
 *     INPUTS(0): The core state UTXO
 *   </li>
 *   <li>
 *     OUTPUTS(0): The core state UTXO with an updated R5 to reflect which investors' Ergs were
 *     used, as well as an updated R7 to track the token's UTXO(s) that are owned by the fund
 *   </li>
 *   <li>
 *     OUTPUTS(1)-OUTPUTS(N): Any number of UTXOs representing tokens which have a guard allowing them
 *     to be spent on the Ergo DEX, provided that the resulting UTXOs from any Ergo DEX transaction will
 *     remain guarded by the script which allows the core state to spend them
 *   </li>
 * </ul>
 */
object BuyTokenContract {
  def run() = {
  }
}
