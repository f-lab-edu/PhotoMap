package ny.photomap.data.model

interface ModelMapper<T> {
    fun toModel(): T
}