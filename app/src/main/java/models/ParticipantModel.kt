package models

data class ParticipantModel(
    var name: String = "",
    var email: String = "",
    var phoneNo: Long = 0,
    var promoCode: String = "",
    var quantity: Int = 0
)

fun main() {
    // Creating an instance of ParticipantModel
    val participant = ParticipantModel(
        name = "Alice Johnson",
        email = "alice.johnson@example.com",
        phoneNo = 9876543210,
        promoCode = "WELCOME10",
        quantity = 3
    )

    // Accessing properties
    println("Name: ${participant.name}") // Output: Name: Alice Johnson
    println("Email: ${participant.email}") // Output: Email: alice.johnson@example.com
    println("Phone No: ${participant.phoneNo}") // Output: Phone No: 9876543210
    println("Promo Code: ${participant.promoCode}") // Output: Promo Code: WELCOME10
    println("Quantity: ${participant.quantity}") // Output: Quantity: 3

    // Updating the quantity
    participant.quantity += 2
    println("Updated Quantity: ${participant.quantity}") // Output: Updated Quantity: 5

    // Copying the instance with modified quantity
    val newParticipant = participant.copy(quantity = 4)
    println("New Participant Quantity: ${newParticipant.quantity}") // Output: New Participant Quantity: 4
}
