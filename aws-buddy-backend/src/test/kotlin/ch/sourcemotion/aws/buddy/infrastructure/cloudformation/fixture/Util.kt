package ch.sourcemotion.aws.buddy.infrastructure.cloudformation.fixture

import software.amazon.awssdk.services.cloudformation.model.Parameter

fun parameterOf(key: String, value: String): Parameter =
    Parameter.builder().parameterKey(key).parameterValue(value).build()
