<script setup>
</script>

<template>
    <header class="layout-header">
        <nav>
            <!-- Domain link with site name -->
            <a class="domain" href="/">
                <span>Duck Online Judge</span>
            </a>
            <!-- Navigation items -->
            <div class="nav-items">
                <a class="nav-item active" href="/"><i class="fas fa-home"></i> 主页</a>
                <a class="nav-item" href="#"><i class="fas fa-book"></i> 题库</a>
                <a class="nav-item" href="#"><i class="fas fa-trophy"></i> 竞赛</a>
                <a class="nav-item" href="#"><i class="fas fa-tasks"></i> 状态</a>
                <a class="nav-item" href="#"><i class="fas fa-chart-bar"></i> 排名</a>
                <a class="nav-item" href="#"><i class="fas fa-info-circle"></i> 关于</a>
            </div>
            <!-- Authentication buttons -->
            <div class="auth-buttons">
                <button class="login"><i class="fas fa-sign-in-alt"></i> <a href="/login">登录</a></button>
                <button class="register"><i class="fas fa-user-plus"></i> <a href="/register">注册</a></button>
            </div>
        </nav>
    </header>
</template>

<style scoped lang="scss">
/* Header styling */
.layout-header {
    background-color: #f7f7f7;
    /* Light background color */
    padding: 0;
    /* Remove padding */
    width: 100%;
    /* Full width */
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    /* Subtle shadow */
    height: 60px;
    /* Fixed height for the header */

    /* Navigation styling */
    nav {
        display: flex;
        /* Flexbox layout for navigation */
        flex-direction: row;
        /* Horizontal layout */
        justify-content: space-between;
        /* Space out items */
        align-items: center;
        /* Center items vertically */
        padding: 0 75px;
        /* Horizontal padding */
        height: 100%;
        /* Full height of the header */
    }

    /* Domain link styling */
    .domain {
        font-size: 20px;
        /* Larger font size for site name */
        font-weight: bold;
        /* Bold text */
        color: #333;
        /* Dark text color */
        text-decoration: none;
        /* Remove underline */
    }

    /* Navigation items container styling */
    .nav-items {
        display: flex;
        /* Flexbox layout for items */
        gap: 30px;
        /* Spacing between items */
        margin-right: 120px;
        /* Margin to align with the auth-buttons */
        margin-top: 18px;
        /* Align items vertically */
    }

    /* Individual navigation item styling */
    .nav-item {
        text-decoration: none;
        /* Remove underline */
        color: #333;
        /* Dark text color */
        font-size: 17px;
        /* Font size for items */
        position: relative;
        /* For positioning active border */

        i {
            margin-right: 5px;
            /* Space between icon and text */
        }

        /* Active state styling */
        &.active {
            color: #2d8cf0;
            /* Active text color */
            border-bottom: 5px solid #2d8cf0;
            /* Underline effect */
            padding-bottom: 13px;
            /* Padding to position underline */
        }

        /* Hover state styling */
        &:hover {
            color: #007bff;
            /* Hover text color */
        }
    }

    /* Authentication buttons styling */
    .auth-buttons {
        button {
            margin-left: 10px;
            /* Space between buttons */
            padding: 5px 15px;
            /* Button padding */
            border-radius: 50px;
            /* Rounded corners */
            cursor: pointer;
            /* Pointer cursor on hover */

            i {
                margin-right: 5px;
                /* Space between icon and text */
            }

            /* Button hover state styling */
            &:hover {
                background-color: #0056b3;
                /* Darker background on hover */
            }
        }

        /* Login button styling */
        .login {
            border: 1px solid #5f5f60;
            /* Border color */
            background-color: #5f5f60;
            color: #fff;
            a {
                color: #fff;
            }
        }

        /* Register button styling */
        .register {
            border: 1px solid #28a745;
            /* Border color */
            background-color: #28a745;
            color: #fff;
            a {
                color: #fff;
            }
        }
    }
}</style>