package xcode.bracketing.domain.model

object CurrentAuth {

    private val USER_HOLDER: ThreadLocal<Token> = ThreadLocal<Token>()

    @Synchronized
    fun set(tokenDto: Token) {
        USER_HOLDER.set(tokenDto)
    }

    fun get(): Token {
        return USER_HOLDER.get()
    }

    @Synchronized
    fun remove() {
        USER_HOLDER.remove()
    }
}