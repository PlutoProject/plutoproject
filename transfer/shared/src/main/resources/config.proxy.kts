condition {
    destination("survival") {
        errorMessage {
            text("× 不支持基岩版") without italic() with mochaMaroon
        }
        checker {
            !it.isBedrock
        }
    }
}