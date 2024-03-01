import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ink.pmc.common.member.MemberImpl;

@JsonDeserialize(as = MemberImpl.class)
public abstract class MixinTest {
}
