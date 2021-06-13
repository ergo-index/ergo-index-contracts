package contracts

/**
 * Investor Joins Tx: Accepts a new investor into the fund, increasing the pool of UTXOs spendable
 * by the core state UTXO
 * <ul>
 *   <li>
 *     INPUTS(0): The core state UTXO
 *   </li>
 *   <li>
 *     INPUTS(1)-INPUTS(N): Any number of UTXOs belonging to the new investor
 *   </li>
 *   <li>
 *     OUTPUTS(0): The core state UTXO with an updated R5 to reflect the new investor's shares.
 *   </li>
 *   <li>
 *     OUTPUTS(1)-OUTPUTS(N): Any change UTXOs for the new investor
 *   </li>
 * </ul>
 */
object JoinInvestorContract {
  def run() = {
  }
}
