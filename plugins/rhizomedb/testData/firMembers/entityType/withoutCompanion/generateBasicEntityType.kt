// File generated by `org.jetbrains.rhizomedb.TestGeneratorKt`. DO NOT MODIFY MANUALLY
import com.jetbrains.rhizomedb.*

@GeneratedEntityType(EntityType::class)
data class MyEntity(override val eid: EID) : Entity

fun foo() {
    MyEntity.all()
    MyEntity.single()
    MyEntity.singleOrNull()
}