package com.example.polyscrabbleclient.account.model

data class UpdateResponse(val errors: List<String>?)

data class ConnectionLogResponse(val pagination: Pagination, val logs: ArrayList<ConnectionLog>)

