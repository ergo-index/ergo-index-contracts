import contracts.CreateIndexContract
import org.ergoplatform.playground._
import org.ergoplatform.playgroundenv.models.TokenInfo

import scala.language.postfixOps

/**
 * Entry point to demonstrate a proof-of-concept for creating index and mutual funds.
 * This program runs on a mockchain, but it has the foundation for some of the contracts
 * that would be put into a P2S address and deployed to the mainnet.
 * <br /><br />
 * We use the outsourced computation protocol design pattern described
 * <a href="https://github.com/Emurgo/Emurgo-Research/blob/master/smart-contracts/High%20Level%20Design%20Patterns%20In%20Extended%20UTXO%20Systems.md">here</a>.
 * See the "registers" package to view the data stored in our core state UTXO. There are 6 main transactions
 * that result in the output of a new core state UTXO:
 * <br /><br />
 * <ul>
 *   <li>
 *     Create Fund Tx: initializes the fund. Only happens once for each fund
 *     <ul>
 *       <li>
 *         INPUTS(0)-INPUTS(N): Any number of UTXOs guarded by the fund creator's public key
 *       </li>
 *       <li>
 *         OUTPUTS(0): The core state UTXO with portfolio configuration information populated in its registers, as
 *         well as an initial investment for the fund
 *       </li>
 *       <li>
 *         OUTPUTS(1)-OUTPUTS(N): Any change UTXOs for the fund owner
 *       </li>
 *     </ul>
 *     <br />
 *   </li>
 *   <li>
 *     Buy Token Tx: creates a UTXO that can be used on the Ergo DEX to purchase an NFT or token. This
 *     will hopefully be able to work with other blockchains in the future to provide native assets in addition
 *     to wrapped assets (possibly by using THORchain, Gravity, and/or IBC protocol).
 *     <ul>
 *       <li>
 *         INPUTS(0): The core state UTXO
 *       </li>
 *       <li>
 *         OUTPUTS(0): The core state UTXO with an updated R5 to reflect which investors' Ergs were
 *         used, as well as an updated R7 to track the token's UTXO(s) that are owned by the fund
 *       </li>
 *       <li>
 *         OUTPUTS(1)-OUTPUTS(N): Any number of UTXOs representing tokens which have a guard allowing them
 *         to be spent on the Ergo DEX, provided that the resulting UTXOs from any Ergo DEX transaction will
 *         remain guarded by the script which allows the core state to spend them
 *       </li>
 *     </ul>
 *     <br />
 *   </li>
 *   <li>
 *     Sell Token Tx: Sells a token that is tracked in the core state's R7 and creates a new UTXO that is
 *     spendable by the fund (i.e., the core state UTXO)
 *     <ul>
 *       <li>
 *         INPUTS(0): The core state UTXO
 *       </li>
 *       <li>
 *         INPUTS(1): The UTXO of a token that the fund owns (and is consequentially stored in R7)
 *       </li>
 *       <li>
 *         OUTPUTS(0): The core state UTXO with an updated R5 to reflect which investors' Ergs were
 *         moved back into the core state UTXO, as well as an updated R7 to reflect the fact that one less UTXO
 *         is now spendable by the fund.
 *       </li>
 *     </ul>
 *     <br />
 *   </li>
 *   <li>
 *     Investor Joins Tx: Accepts a new investor into the fund, increasing the pool of UTXOs spendable
 *     by the core state UTXO
 *     <ul>
 *       <li>
 *         INPUTS(0): The core state UTXO
 *       </li>
 *       <li>
 *         INPUTS(1)-INPUTS(N): Any number of UTXOs belonging to the new investor
 *       </li>
 *       <li>
 *         OUTPUTS(0): The core state UTXO with an updated R5 to reflect the new investor's shares.
 *       </li>
 *       <li>
 *         OUTPUTS(1)-OUTPUTS(N): Any change UTXOs for the new investor
 *       </li>
 *     </ul>
 *     <br />
 *   </li>
 *   <li>
 *     Investor Liquidates Tx: Returns funds to an investor (up to the full value of his/her shares),
 *     spending some of the fund's invested tokens if necessary
 *     <ul>
 *       <li>
 *         INPUTS(0): The core state UTXO
 *       </li>
 *       <li>
 *         INPUTS(1)-INPUTS(N): The UTXOs of tokens that the fund owns (and are consequentially stored in R7).
 *         R7 must show that each token is at least partially owned by the investor.
 *       </li>
 *       <li>
 *         OUTPUTS(0): The core state UTXO with updated R5 and R7 to reflect the investor's updated shares, as well
 *         as the fund's new amount of tokens if the Tx needed to spend some. If the investor was only partially
 *         in some of the tokens, then the change will go back into the fund to be invested in more tokens in
 *         a future Buy Token Tx, and R5 will reflect this
 *       </li>
 *       <li>
 *         OUTPUTS(1): A UTXO with the value that the investor liquidated
 *       </li>
 *     </ul>
 *     <br />
 *   </li>
 *   <li>
 *     Update Portfolio Tx: Allows the manager to rebalance the fund's portfolio
 *     <ul>
 *       <li>
 *         INPUTS(0): The core state UTXO
 *       </li>
 *       <li>
 *         OUTPUTS(0): The core state UTXO with updated R6. No other registers are allowed to be updated
 *       </li>
 *     </ul>
 *     <br />
 *   </li>
 * </ul>
 * <br />
 * Each of these transactions (with the exception of the initial creation Tx) takes the core state UTXO
 * in INPUTS(0) and outputs an updated version of it in OUTPUTS(0). Please refer to the visual diagram of
 * this at <a href="https://ergo-index.fund">https://ergo-index.fund</a>
 */
object Main {
  def main(args: Array[String]): Unit = {

    // Create a simulated blockchain (aka "Mockchain")
    val blockchainSim = newBlockChainSimulationScenario("Ergo Index")

    // Class that holds all fund data/info & passes object around classes.
    // Attempting to pass solely the info each box needs to verify transactions
    // rather than passing entire maps
    class fundInfo(tokenInfo: TokenInfo) {

      // R4: an NFT to ensure uniqueness from other core UTXO's
      var nft = TokenInfo

      // R5: a map of public keys and the amount of ERG's owned by them (in nanoErgs)
      var pks: Map[Address, Long] = Map()

      // R6: a map of token/coin tickers and an object containing info about the token/coin
      var tokensData: Map[String, tokenInfoObj] = Map(
        "ERG" -> new tokenInfoObj(51.5, 7.83, 12.25),
        "ALT" -> new tokenInfoObj(49.5, 4.22, 18.0))

      // R7: a map of hashes and/or ID's of UTXO's representing tokens the fund purchased
      var utxos: Map[String, String] = Map("asfdjklqwefuiop" -> "3aavkewru8asdv8403wasd")

      class tokenInfoObj(percentNav1: Double, buyTarget1: Double, sellTarget1: Double) {
        var percentNav: Double = percentNav1
        var buyTarget: Double = buyTarget1
        var sellTarget: Double = sellTarget1
      }
    }

    val nft = blockchainSim.newToken("nft")

    val fundInfo1 = new fundInfo(nft)



    //Initiating the CreateIndexContract with the fund's founder
    val fundAssetsBox = CreateIndexContract.run(blockchainSim)

    //BuyTokenContract.run(blockchainSim, fundAssetsBox)

    //set minTxFee
    val MinTxFee = 1000000L

    // Define UTXO registers

    // R4: an NFT to ensure uniqueness from other core UTXO's

    // R5: a map of public keys and the amount of ERG's owned by them (in nanoErgs)
    //var pks: scala.collection.Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map(fundFounderAddress -> fundFounderBal, address1 -> 10000000L, address2 -> 20000000L)
    // collection of collection of bytes

    // R6: a map of token/coin tickers and an object containing info about the token/coin
    // TODO: Write API call for token info data + map out defining manager instructions for setting object fields

    // R8: a map of guard scripts for each transaction type
    //var guardScripts: scala.collection.Map[String, String] = Map("buy" -> buyTokenScript, "sell" -> sellTokenScript, "joinInvestor" -> joinInvestorScript, "liqInvestor" -> liqInvestorScript, "updateSettings" -> updateSettingsScript)

    // R9 isn't allocated
    var randomVar: Int = 1


    ///////////////////////////////////////////////////////////////////////////////////
    // Scripts //
    ///////////////////////////////////////////////////////////////////////////////////
    // Scripts Implemented:
    //     1) fundingScript
    //     2) buyTokenScript
    //     3) sellTokenScript
    //     4) joinInvestorScript
    //     5) liqInvestorScript
    //     6) updateSettingsScript


  }
}