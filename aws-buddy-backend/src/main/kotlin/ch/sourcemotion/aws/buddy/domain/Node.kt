package ch.sourcemotion.aws.buddy.domain

interface Node {
    val spec: NodeSpec
}

interface NodeSpec {
    val label: String
}