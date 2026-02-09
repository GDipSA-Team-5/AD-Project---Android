package iss.nus.edu.sg.appfiles.mobile_ewaste

import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.rewards.RewardsRules
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RewardsRulesTest {

    @Test
    fun canRedeem_returnsTrue_whenEnoughPointsAndAvailableAndInStock() {
        val result = RewardsRules.canRedeem(
            availablePoints = 1200,
            rewardPoints = 500,
            rewardAvailable = true,
            stockQuantity = 10
        )

        assertTrue(result)
    }

    @Test
    fun canRedeem_returnsFalse_whenInsufficientPoints() {
        val result = RewardsRules.canRedeem(
            availablePoints = 100,
            rewardPoints = 500,
            rewardAvailable = true,
            stockQuantity = 10
        )

        assertFalse(result)
    }

    @Test
    fun canRedeem_returnsFalse_whenOutOfStock() {
        val result = RewardsRules.canRedeem(
            availablePoints = 1200,
            rewardPoints = 500,
            rewardAvailable = true,
            stockQuantity = 0
        )

        assertFalse(result)
    }

    @Test
    fun canRedeem_returnsFalse_whenRewardMarkedUnavailable() {
        val result = RewardsRules.canRedeem(
            availablePoints = 1200,
            rewardPoints = 500,
            rewardAvailable = false,
            stockQuantity = 10
        )

        assertFalse(result)
    }

    @Test
    fun canRedeem_returnsTrue_whenPointsExactlyEqual() {
        val result = RewardsRules.canRedeem(
            availablePoints = 500,
            rewardPoints = 500,
            rewardAvailable = true,
            stockQuantity = 1
        )

        assertTrue(result)
    }

    @Test
    fun canRedeem_returnsFalse_whenStockIsNegative() {
        val result = RewardsRules.canRedeem(
            availablePoints = 500,
            rewardPoints = 200,
            rewardAvailable = true,
            stockQuantity = -1
        )

        assertFalse(result)
    }
}
