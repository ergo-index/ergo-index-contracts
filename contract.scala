import org.ergoplatform.compiler.ErgoScalaCompiler._
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.playground._
import org.ergoplatform.Pay2SAddress
import sigmastate.eval.Extensions._
import scorex.crypto.hash.{Blake2b256}
//import scala.collection.mutable.Map
import scala.language.postfixOps
import sigmastate.eval.SigmaDsl



// Create a simulated blockchain (aka "Mockchain")
val blockchainSim = newBlockChainSimulationScenario("Ergo Index")

// The creation of each of the investors
// Website will pass investor data here
// TODO: write API calls for new investor data
val fundFounder = blockchainSim.newParty("fundFounds") // fundFounder's wallet would, in theory, be initialized before the fund's creation
val fundFounderBalance = 1000000000L
val fundFounderAddress = fundFounder.wallet.getAddress
val rawFundFounder = SigmaDsl.GroupElement(fundFounderAddress.proveDlog.value)
fundFounder.generateUnspentBoxes(toSpend = fundFounderBalance) // generating unspent UTXO boxes

val fund = blockchainSim.newParty("fund")
val fundAddress = fund.wallet.getAddress
val rawFund = SigmaDsl.GroupElement(fundAddress.proveDlog.value)

val investor1 = blockchainSim.newParty("1-investor")
val investor1Bal = 1000000000L 
val address1 = investor1.wallet.getAddress
val raw1 = SigmaDsl.GroupElement(address1.proveDlog.value)

val investor2 = blockchainSim.newParty("2-investor")
val investor2Bal = 1000000000L
val address2 = investor2.wallet.getAddress
val raw2 = SigmaDsl.GroupElement(address2.proveDlog.value)


// Object of token info (defined in R5)
class tokenInfoObj(percentNav1: Double, buyTarget1: Double, sellTarget1: Double) {
  var percentNav: Double = percentNav1
  var buyTarget: Double = buyTarget1
  var sellTarget: Double = sellTarget1
}

// Define UTXO registers

// R4: an NFT to ensure uniqueness from other core UTXO's
val nft = blockchainSim.newToken("nft")

// R5: a map of public keys and the amount of ERG's owned by them (in nanoErgs)
var pks: scala.collection.Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map(fundFounderAddress -> fundFounderBalance, address1 -> 10000000L, address2 -> 20000000L)
// collection of collection of bytes

// R6: a map of token/coin tickers and an object containing info about the token/coin
// TODO: Write API call for token info data + map out defining manager instructions for setting object fields
var tokensData: scala.collection.Map[String, tokenInfoObj] = Map("ERG" -> new tokenInfoObj(51.5, 7.83, 12.25), "ALT" -> new tokenInfoObj(49.5, 4.22, 18.0))

// R7: a map of hashes and/or ID's of UTXO's representing tokens the fund purchased
var utxos: scala.collection.Map[String, String] = Map("asfdjklqwefuiop" -> "3aavkewru8asdv8403wasd")

// R8: a map of guard scripts for each transaction type
var guardScripts: scala.collection.Map[String, String] = Map("buy" -> buyTokenScript, "sell" -> sellTokenScript, "joinInvestor" -> joinInvestorScript, "liqInvestor" -> liqInvestorScript, "updateSettings" -> updateSettingsScript)

// R9 isn't allocated
var randomVar: Int = 1


///////////////////////////////////////////////////////////////////////////////////
// Scripts //
///////////////////////////////////////////////////////////////////////////////////
// Scripts Implemented Below:
//     1) fundingScript
//     2) buyTokenScript
//     3) sellTokenScript
//     4) joinInvestorScript
//     5) liqInvestorScript
//     6) updateSettingsScript

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


// buyTokenScript will lock the pooled fund box from being spent unelss the following are true:
//     1) Ring signature provided
//     2) 
val buyTokenScript =
  s"""
  {
  OUTPUTS(1).propositionBytes == founderPk.propBytes
  }
  """.stripMargin

val buyTokenContract = ErgoScriptCompiler.compile(Map("founderPk" -> fundFounderAddress.pubKey), buyTokenScript)


// sellTokenScript will prevent a purchased token's box from being sold/liquidated unless the following are true:
//     1) Ring signature provided
//     2) Input's guard script == Output(1)'s propositionBytes
//     3) Output(0)'s array of hashes (aka R7.length) is 1 less than Input(0)'s array of hashes <- not sure if entirely possible to check
//     4) The hash of the token sold !isDefined in Output(0)'s array of hashes
val sellTokenScript = 
  s"""
  {
  1
  }
  """.stripMargin

val sellTokenContract = ErgoScriptCompiler.compile(Map(), sellTokenScript)



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




// liqInvestorScript will lock the pooled fund box from being spent unless the following are true:
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



// updateSettingsScript will lock the pooled fund box from being spent unless the following are true:
//     1) FundFounder signature provided
//     2) 
val updateSettingsScript =
  s"""
  {
  1
  }
  """.stripMargin

val updateSettingsContract = ErgoScriptCompiler.compile(Map(), updateSettingsScript)


///////////////////////////////////////////////////////////////////////////////////
// Create Index Transaction //
///////////////////////////////////////////////////////////////////////////////////
// Every created "create index" UTXO has 1 input box (a random UTXO from the fundFounder) and produces 1 output box (fundAssetsBox)

// This transaction creates the uniquely-identifiable fund, loads it with the founder's funds and sends it to the blockchain!

val fundAssetsBox = Box(value = fundFounderBalance - MinTxFee, 
                        registers = Map(R4 -> nft, R5 -> pks, R6 -> tokensData, R7 -> utxos, R8 -> guardScripts),
                        script = fundingContract
                       )

val createIndexTx  = Transaction (
      inputs       = fundFounder.selectUnspentBoxes(toSpend = fundFounderBalance), //TODO: Remove "+ 1000000" & figure out why inputs ≠ outputs
      outputs      = List(fundAssetsBox),
      fee          = MinTxFee,
      sendChangeTo = fundFounderAddress
)

///////////////////////////////////////////////////////////////////////////////////
// Buy Token Transaction //
///////////////////////////////////////////////////////////////////////////////////
// Every created "buy token" box has 1 input box (the pooled fund box) and produces 2 output boxes 
// (one being being a UTXO holding the newly-purchased token & the other being the recursed pooled-fund box)
val tokenPrice: Long = 10000000/2


// This function will loop through pks, find the first investor that has enough funds to purchase the 
def  purchaseToken(tokenPrice: Long, pks: Map[org.ergoplatform.playgroundenv.models.Address, Long]) : scala.collection.immutable.Map[org.ergoplatform.playgroundenv.models.Address, Long] = {
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
        var tokensData1: Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map(fundFounderAddress -> fundFounderBalance, address1 -> 10000000L, address2 -> 20000000L)
   return tokensData1
}

println("-----------")
fundFounder.printUnspentAssets()

//var immMap: Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map(fundFounderAddress -> fundFounderBalance, address1 -> 10000000L, address2 -> 20000000L)
  var immMap: scala.collection.immutable.Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map(fundFounderAddress -> (fundFounderBalance - tokenPrice), address1 -> 10000000L, address2 -> 20000000L)
// should be partyInstance.printUnspentAssets()
//println(immMap.get(fundFounderAddress))
fundFounder.printUnspentAssets()
println("-----------")


val buyOutput = Box(
  value = fundFounderBalance - tokenPrice,
  registers = Map(R5 -> immMap),
  script = sellTokenContract
)

val buyTokenTransaction = Transaction(
      inputs = fundFounder.selectUnspentBoxes(toSpend = fundFounderBalance),
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

/*val investorJoinsTransaction = Transaction (
      inputs       = fundAssetsBox,
      outputs      = List(fundAssetsBox),
      fee          = MinTxFee,
      sendChangeTo = address1
)*/













/*
///////////////////////////////////////////////////////////////////////////////////
// Buy Token Transaction //
///////////////////////////////////////////////////////////////////////////////////
// Every created "buy token" box has 1 input box (the pooled fund box) and produces 2 output boxes 
// (one being being a UTXO holding the newly-purchased token & the other being the recursed pooled-fund box)

// an output box that holds:
// 1) the newly-purchased token 
// 2) the guard script the box's release/liquidation
// TODO: Fix value field
val buyOutput = Box(value = 1, // As each contract will only hold 1 token
                          script = liquidateLockContract)

// Create the deposit transaction which locks the users funds under the contract
// TODO: input CORE state as input & change input to List(pks, CORE_state)
val userToBuy = pks.get(getBuyer())
val buyTx = Transaction(
      inputs       = pks.get(userToBuy).selectUnspentBoxes(pks.get(userToBuy).getFunds()) // May be wrong, I believe you just take in the pooled-fund box
      outputs      = List(LiquidateBox), // + the recursed pooled-fund box
      fee          = MinTxFee,
      sendChangeTo = userToBuy.wallet.getAddress
    )

// Print depositTransaction
//println(depositTransaction)
// Sign the depositTransaction
//val depositTransactionSigned = userParty.wallet.sign(depositTransaction)
// Submit the tx to the simulated blockchain
//blockchainSim.send(depositTransactionSigned)
//userParty.printUnspentAssets()
println("-----------")
*/


/*
///////////////////////////////////////////////////////////////////////////////////
// Liquidate Funds Transaction //
///////////////////////////////////////////////////////////////////////////////////
// a "liquidate funds" transaction produces 
// outputs 2 boxes, one box for the investor's individual funds + the recursed pooled-fund box
// inputs is  
// Subtracts `MinTxFee` from value to account for tx fee which
// must be paid.
val LiquidateBox      = Box(value = 1,
                          script = contract(userParty.wallet.getAddress.pubKey),
                          register = (R4 -> pinNumber.getBytes()))

// Create the withdrawTransaction
val LiquiddateTx = Transaction(
      inputs       = List(depositTransactionSigned.outputs(0)),
      outputs      = List(withdrawBox),
      fee          = MinTxFee,
      sendChangeTo = userParty.wallet.getAddress
    )
// Print withdrawTransaction
println(withdrawTransaction)
// Sign the withdrawTransaction
val withdrawTransactionSigned = userParty.wallet.sign(withdrawTransaction)
// Submit the withdrawTransaction
blockchainSim.send(withdrawTransactionSigned)

// Print the user's wallet, which shows that the coins have been withdrawn (with same total as initial, minus the MinTxFee * 2)
userParty.printUnspentAssets()
println("-----------")
*/
