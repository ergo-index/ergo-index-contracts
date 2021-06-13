import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.playgroundenv.models.BlockchainSimulation
import scala.language.postfixOps

object UpdateSettingsContract {
  def run(blockchainSim: BlockchainSimulation): Unit = {
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