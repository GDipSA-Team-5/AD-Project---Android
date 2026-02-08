package iss.nus.edu.sg.appfiles.mobile_ewaste.ui.rewards

object RewardsRules {
    fun canRedeem(
        availablePoints: Int,
        rewardPoints: Int,
        rewardAvailable: Boolean,
        stockQuantity: Int
    ): Boolean {
        return rewardAvailable && stockQuantity > 0 && rewardPoints <= availablePoints
    }
}
