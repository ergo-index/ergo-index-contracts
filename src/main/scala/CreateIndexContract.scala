import org.ergoplatform.playground.{Box, Transaction}
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler

object CreateIndexContract {
  def run(): Unit = {
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

    val createIndexTx = Transaction(
      inputs = fundFounder.selectUnspentBoxes(toSpend = fundFounderBal / 100),
      outputs = List(fundAssetsBox),
      fee = MinTxFee,
      sendChangeTo = fundFounderAddress
    )

    val createIndexTxSigned = fundFounder.wallet.sign(createIndexTx)
    blockchainSim.send(createIndexTxSigned)
    fundFounder.printUnspentAssets()

  }
}