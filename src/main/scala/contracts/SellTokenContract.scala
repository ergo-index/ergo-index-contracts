package contracts

/*import org.ergoplatform.playground.{Box, R5, Transaction}
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.compiler.ErgoScalaCompiler._
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.playground._
import org.ergoplatform.Pay2SAddress
import org.ergoplatform.playgroundenv.models.BlockchainSimulation
import sigmastate.eval.Extensions._
import scorex.crypto.hash.Blake2b256
//import scala.collection.mutable.Map
import scala.language.postfixOps
import sigmastate.eval.SigmaDsl

object SellTokenContract {
  def run(blockchainSim: BlockchainSimulation): Unit = {
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

  }
}*/
