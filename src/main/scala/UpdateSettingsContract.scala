import org.ergoplatform.playground.{Box, R5, Transaction}
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler

import org.ergoplatform.compiler.ErgoScalaCompiler._
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.playground._
import org.ergoplatform.Pay2SAddress
import sigmastate.eval.Extensions._
import scorex.crypto.hash.{Blake2b256}
//import scala.collection.mutable.Map
import scala.language.postfixOps
import sigmastate.eval.SigmaDsl

object UpdateSettingsContract {
  def run(): Unit = {
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