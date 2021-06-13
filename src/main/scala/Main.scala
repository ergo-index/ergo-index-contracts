import org.ergoplatform.playground._
import org.ergoplatform.playgroundenv.models.TokenInfo
import scala.language.postfixOps

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

    // Object of token info (defined in R5)

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