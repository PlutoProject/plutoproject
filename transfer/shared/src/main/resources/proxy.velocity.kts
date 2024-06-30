condition {
    destination("survival") {
        errorMessage {
            text("不支持基岩版") with mochaMaroon
        }
        checker {
            !it.isBedrock
        }
    }
}