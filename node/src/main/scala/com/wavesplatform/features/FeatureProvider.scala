package com.wavesplatform.features

import com.wavesplatform.block.Block.{NgBlockVersion, PlainBlockVersion, RewardBlockVersion}
import com.wavesplatform.state.Blockchain

object FeatureProvider {
  implicit class FeatureProviderExt(provider: Blockchain) {
    def isFeatureActivated(feature: BlockchainFeature, height: Int = provider.height): Boolean =
      provider.activatedFeatures.get(feature.id).exists(_ <= height)

    def activatedFeaturesAt(height: Int): Set[Short] =
      provider.activatedFeatures.collect {
        case (featureId, activationHeight) if height >= activationHeight => featureId
      }.toSet

    def featureStatus(feature: Short, height: Int): BlockchainFeatureStatus =
      if (provider.activatedFeatures.get(feature).exists(_ <= height)) BlockchainFeatureStatus.Activated
      else if (provider.approvedFeatures.get(feature).exists(_ <= height)) BlockchainFeatureStatus.Approved
      else BlockchainFeatureStatus.Undefined

    def currentBlockVersion: Byte =
      if (isFeatureActivated(BlockchainFeatures.BlockReward)) RewardBlockVersion
      else if (provider.settings.functionalitySettings.blockVersion3AfterHeight < provider.height) NgBlockVersion
      else PlainBlockVersion

    def featureActivationHeight(feature: Short): Option[Int] = provider.activatedFeatures.get(feature)
    def featureApprovalHeight(feature: Short): Option[Int]   = provider.approvedFeatures.get(feature)
  }
}
