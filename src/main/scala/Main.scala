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




    ///////////////////////////////////////////////////////////////////////////////////
    // Create Index Transaction //
    ///////////////////////////////////////////////////////////////////////////////////
    // Every created "create index" UTXO has 1 input box (a random UTXO from the fundFounder) and produces 1 output box (fundAssetsBox)

    // This transaction creates the uniquely-identifiable fund, loads it with the founder's funds and sends it to the blockchain!
    println("STARTING CREATE INDEX TX")

    // fundingScript will lock the pooled fund box from being spent unless the following are true:
    //     1) Ring signature provided
    //     2) The guard script held in Output(1) isDefined in R8
    //     3) That the investor's funds have already been subtracted/added in the Output by comparing the investor's balance in both the input & output
    //     4) If the output's guard script == "buy" then Output(1).value == (NAV / R6["Token"].targetNAV) * 100
    //     5) If the output's guard script == "buy" || "sell" then:
    //        a) Check that the target buy/sell price is within 1% of the target buy/sell price
    val fundingScript =
    s"""
  {
     OUTPUTS(1).propositionBytes
  }
""".stripMargin

    val fundingContract = ErgoScriptCompiler.compile(Map("founderPk" -> fundFounderAddress.pubKey), fundingScript)

    val fundAssetsBox = Box(value = fundFounderBal - MinTxFee,
      registers = Map(), // R4 -> nft, R5 -> pks, R6 -> tokensData, R7 -> utxos, R8 -> guardScripts
      script = fundingContract
    )

    val createIndexTx  = Transaction (
      inputs       = fundFounder.selectUnspentBoxes(toSpend = fundFounderBal/100),
      outputs      = List(fundAssetsBox),
      fee          = MinTxFee,
      sendChangeTo = fundFounderAddress
    )

    val createIndexTxSigned = fundFounder.wallet.sign(createIndexTx)
    blockchainSim.send(createIndexTxSigned)
    fundFounder.printUnspentAssets()




    ///////////////////////////////////////////////////////////////////////////////////
    // Buy Token Transaction //
    ///////////////////////////////////////////////////////////////////////////////////
    // Every created "buy token" box has 1 input box (the pooled fund box) and produces 2 output boxes
    // (one being being a UTXO holding the newly-purchased token & the other being the recursed pooled-fund box)

    // buyTokenScript will lock the pooled fund box from being spent unelss the following are true:
    //     1) Ring signature provided, and many, many more
    val buyTokenScript =
    s"""
  {
  OUTPUTS(1).propositionBytes == founderPk.propBytes
  }
  """.stripMargin

    val buyTokenContract = ErgoScriptCompiler.compile(Map("founderPk" -> fundFounderAddress.pubKey), buyTokenScript)


    val tokenPrice: Long = 10000000/2


    // This function will loop through pks, find the first investor that has enough funds to purchase the
    /*def  purchaseToken(tokenPrice: Long, pks: Map[org.ergoplatform.playgroundenv.models.Address, Long]) : scala.collection.immutable.Map[org.ergoplatform.playgroundenv.models.Address, Long] = {
        var tokensData: Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map()
        pks.keys.foreach{ i =>
          print("Key = " + i)
          println(" Value = " + pks(i))
          //tokensData += i -> pks(i)
          //tokensData.map(kv => (kv._1,kv._2.toSet)).toMap
        }
        //var immMap: scala.collection.immutable.Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map())
          //printf("key: %s, value: %s\n", k, v)
          //if(v >= tokenPrice)
            var tokensData1: Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map(fundFounderAddress -> fundFounderBal, address1 -> 10000000L, address2 -> 20000000L)
       return tokensData1
    }*/

    println("-----------")
    fundFounder.printUnspentAssets()

    var immutableMap: Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map(fundFounderAddress -> fundFounderBal, address1 -> 100000000L, address2 -> 200000000L)

    def getNAV(immutableMap: Map[org.ergoplatform.playgroundenv.models.Address, Long]): Long = {
      val nav: Long = 0L
      val i = immutableMap.toIterator
      //immutableMap.keys.foreach { i ->
      for ((k, v) <- immutableMap)
        nav += immutableMap.get(k)
      return nav
    }

    val nav = getNAV(immutableMap)

    val buyOutput = Box(
      value = nav - tokenPrice,
      registers = Map(R5 -> immutableMap),
      script = buyTokenContract // originally had as sellTokenContract
    )

    val buyTokenTransaction = Transaction(
      inputs = fundFounder.selectUnspentBoxes(toSpend = fundFounderBal),
      outputs = List(buyOutput),
      fee     = 1000000L,
      sendChangeTo = fundFounder.wallet.getAddress
    )

    val buyTokenTransactionSigned = fundFounder.wallet.sign(buyTokenTransaction)

    // Submit the tx to the simulated blockchain
    blockchainSim.send(buyTokenTransactionSigned)
    fundFounder.printUnspentAssets()
    println("-----------")


    ///////////////////////////////////////////////////////////////////////////////////
    // Investor Join Transaction //
    ///////////////////////////////////////////////////////////////////////////////////
    // Every created "investorJoin Tx" box has 1 input box and produces 1 output boxes (both the pooled fund box)
    // Website would provide investor's public key & balance
    // TODO: Before doing investor join we need to create the index and send it to the simulated blockchain

    // joinInvestorScript will lock the pooled fund box from being spent unless the following are true:
    //     1) Ring signature provided *(maybe not needed if other investors' permission not need AND/OR new investor can't provide signature yet)
    //     2) new NAV = old NAV + newInvestor's balance
    //     3) new investor array length = old investor array + 1
    val joinInvestorScript =
    s"""
  {
  1
  }
  """.stripMargin

    val joinInvestorContract = ErgoScriptCompiler.compile(Map(), joinInvestorScript)

    /*val investorJoinsTransaction = Transaction (
          inputs       = fundAssetsBox,
          outputs      = List(fundAssetsBox),
          fee          = MinTxFee,
          sendChangeTo = address1
    )*/




    ///////////////////////////////////////////////////////////////////////////////////
    // Sell Token Transaction //
    ///////////////////////////////////////////////////////////////////////////////////
    // Every created "sell token" box has 1 input box (the pooled fund box) and produces 1 output box (both being the recursed pooled-fund box)

    // sellTokenScript will prevent a purchased token's box from being spent unless the following are true:
    //     1) Ring signature provided, and many, many more
    //     2) Input's guard script == Output(1)'s propositionBytes
    //     3) Output(0)'s array of hashes (aka R7.length) is 1 less than Input(0)'s array of hashes <- not sure if entirely possible to check
    //     4) The hash of the token sold !isDefined in Output(0)'s array of hashes
    val sellTokenScript =
    s"""
  {
  pkFounder
  }
  """.stripMargin

    val sellTokenContract = ErgoScriptCompiler.compile(Map("pkFounder" -> fundFounderAddress.pubKey), sellTokenScript)




    ///////////////////////////////////////////////////////////////////////////////////
    // Liquidate Investor Transaction //
    ///////////////////////////////////////////////////////////////////////////////////
    // Every created "liqInvestor" UTXO has 1 input box (a random UTXO from the fundFounder) and produces 2 output boxes (one being investor N's fund box & the other the recursed pooled-fund box)

    // liqInvestorScript will prevent the pooled fund box from being spent unless the following are true:
    //     1) Ring signature provided
    //     2) new Nav = old NAV - oldInvestor's balance
    //     3) new investor array length = old investor array length - 1
    val liqInvestorScript =
    s"""
  {
  1
  }
  """.stripMargin

    val liqInvestorContract = ErgoScriptCompiler.compile(Map(), liqInvestorScript)



    ///////////////////////////////////////////////////////////////////////////////////
    // Update Settings Transaction //
    ///////////////////////////////////////////////////////////////////////////////////

    // updateSettingsScript will lock the pooled fund box from being spent unless the following are true:
    //     1) FundFounder signature provided & some more
    val updateSettingsScript =
    s"""
  {
  1
  }
  """.stripMargin

    val updateSettingsContract = ErgoScriptCompiler.compile(Map(), updateSettingsScript)









  }
}