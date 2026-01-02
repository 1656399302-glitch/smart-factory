<template>
  <div class="user-card">
    <div class="user-avatar u-img-hover">
      <img :src="avatar" alt="userAvatar" />
      <div class="u-magnifier"></div>
    </div>
    <div class="user-info" v-reveal>
      <div class="greeting">{{ timeSlot }}好，{{ user.nickName }}，欢迎来到{{ title }}！</div>
      <div class="slogan">有你创新，创新由你</div>
    </div>
  </div>
</template>

<script>
import { getUserProfile } from "@/api/system/user";
import { mapGetters } from "vuex";
import { getTimeSlot } from "@/utils";

export default {
  name: "UserCard",
  data() {
    return {
      timeSlot: getTimeSlot(),
      user: {},
      title: process.env.VUE_APP_TITLE,
    };
  },
  computed: {
    ...mapGetters(["name", "avatar"]),
  },
  created() {
    this.getUser();
  },
  methods: {
    getUser() {
      getUserProfile().then((response) => {
        this.user = response.data;
      });
    },
  },
};
</script>

<style scoped>
.user-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  flex: 1;
}

.user-avatar {
  display: flex;
  justify-content: center;
  border-radius: var(--radius-full);
  overflow: hidden;
}

.user-avatar img {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-full);
  transition: transform var(--transition-base);
}

.user-info {
  display: inline-flex;
  min-height: 80px;
  flex-direction: column;
  justify-content: center;
  gap: var(--spacing-md);
}

.greeting {
  font-size: var(--font-size-xl);
  color: var(--color-text);
  font-weight: var(--font-weight-bold);
}

.slogan {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
}

@media (max-width: 576px) {
  .user-card {
    gap: var(--spacing-md);
  }

  .user-avatar img {
    width: 60px;
    height: 60px;
  }

  .greeting {
    font-size: var(--font-size-base);
  }

  .slogan {
    font-size: var(--font-size-sm);
  }
}

@media (min-width: 577px) and (max-width: 767px) {
  .user-card {
    gap: var(--spacing-lg);
  }

  .greeting {
    font-size: var(--font-size-lg);
  }
}

@media (min-width: 768px) {
  .user-card {
    gap: var(--spacing-xl);
  }
}
</style>
