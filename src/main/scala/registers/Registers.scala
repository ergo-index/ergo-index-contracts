package registers

/**
 * The data that is stored in each register.
 */
object Registers {
  val R4 = null // NFT info to make sure this core state UTXO is fully unique
  val R5 = null // Mapping of investor public keys to the amounts of Ergs owned by them in the fund
  val R6 = null // Portfolio data (mapping of token IDs to buy/sell target prices and percentage of total portfolio)
  val R7 = null // Mapping of hashes of UTXOs representing tokens the fund purchased
  val R8 = null // Mapping of guard scripts for each transaction type
}
