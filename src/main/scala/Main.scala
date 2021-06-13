import org.ergoplatform.compiler.ErgoScalaCompiler._
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.playground._
import org.ergoplatform.Pay2SAddress
import sigmastate.eval.Extensions._
import scorex.crypto.hash.{Blake2b256}
//import scala.collection.mutable.Map
import scala.language.postfixOps
import sigmastate.eval.SigmaDsl

object Main {
  def main(args: Array[String]): Unit = {

    // Create a simulated blockchain (aka "Mockchain")
    val blockchainSim = newBlockChainSimulationScenario("Ergo Index")

    // The creation of each of the investors
    // Website will pass investor data here
    // TODO: write API calls for new investor data
    val fundFounder = blockchainSim.newParty("fundFounds") // fundFounder's wallet would, in theory, be initialized before the fund's creation
    val fundFounderBal = 200000000000L
    val fundFounderAddress = fundFounder.wallet.getAddress
    fundFounder.generateUnspentBoxes(toSpend = fundFounderBal) // generating unspent UTXO boxes

    val fund = blockchainSim.newParty("fund")
    val fundAddress = fund.wallet.getAddress

    val investor1 = blockchainSim.newParty("1-investor")
    val investor1Bal = 1000000000L
    val address1 = investor1.wallet.getAddress

    val investor2 = blockchainSim.newParty("2-investor")
    val investor2Bal = 1000000000L
    val address2 = investor2.wallet.getAddress

    // Object of token info (defined in R5)
    class tokenInfoObj(percentNav1: Double, buyTarget1: Double, sellTarget1: Double) {
      var percentNav: Double = percentNav1
      var buyTarget: Double = buyTarget1
      var sellTarget: Double = sellTarget1
    }

    //set minTxFee
    val MinTxFee = 1000000L

    // Define UTXO registers

    // R4: an NFT to ensure uniqueness from other core UTXO's
    val nft = blockchainSim.newToken("nft")

    // R5: a map of public keys and the amount of ERG's owned by them (in nanoErgs)
    var pks: scala.collection.Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map(fundFounderAddress -> fundFounderBal, address1 -> 10000000L, address2 -> 20000000L)
    // collection of collection of bytes

    // R6: a map of token/coin tickers and an object containing info about the token/coin
    // TODO: Write API call for token info data + map out defining manager instructions for setting object fields
    var tokensData: scala.collection.Map[String, tokenInfoObj] = Map("ERG" -> new tokenInfoObj(51.5, 7.83, 12.25), "ALT" -> new tokenInfoObj(49.5, 4.22, 18.0))

    // R7: a map of hashes and/or ID's of UTXO's representing tokens the fund purchased
    var utxos: scala.collection.Map[String, String] = Map("asfdjklqwefuiop" -> "3aavkewru8asdv8403wasd")

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