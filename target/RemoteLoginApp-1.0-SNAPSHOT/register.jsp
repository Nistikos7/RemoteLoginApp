<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - RemoteLogin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #1a73e8;
            --secondary-color: #f8f9fa;
        }
        
        body {
            background-color: #f0f2f5;
            height: 100vh;
        }
        
        .register-container {
            max-width: 400px;
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }
        
        .btn-primary {
            background-color: var(--primary-color);
            border-color: var(--primary-color);
            padding: 0.8rem;
            font-weight: 500;
        }
        
        .btn-primary:hover {
            background-color: #1557b0;
            border-color: #1557b0;
        }
        
        .form-control:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 0.2rem rgba(26,115,232,0.25);
        }
        
        .brand-name {
            color: var(--primary-color);
            font-size: 2rem;
            font-weight: bold;
            margin-bottom: 2rem;
        }

        .header-logo {
            position: absolute;
            top: 20px;
            right: 20px;
            text-align: right;
        }
        
        .header-logo img {
            width: 150px;
            height: auto;
            margin-bottom: 10px;
        }
        
        .header-text {
            color: var(--primary-color);
            font-size: 0.9rem;
            font-weight: 500;
        }

        .password-requirements {
            font-size: 0.8rem;
            margin-top: 0.25rem;
        }
    </style>
</head>
<body>
    <div class="header-logo">
        <img src="images/pada.jpg" alt="University of West Attica Logo">
        <div class="header-text">
            ΔΙΚΤΥΑΚΟΣ ΠΡΟΓΡΑΜΜΑΤΙΣΜΟΣ<br>
            ΕΡΓΑΣΙΑ 2024-2025
        </div>
    </div>

    <div class="container d-flex align-items-center justify-content-center" style="height: 100vh;">
        <div class="register-container">
            <div class="text-center mb-4">
                <div class="brand-name">RemoteLogin</div>
                <p class="text-muted">Create your account</p>
            </div>
            
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger" role="alert">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <form action="register" method="post" id="registerForm">
                <div class="mb-3">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" class="form-control" id="username" name="username" required>
                </div>
                
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" required>
                </div>
                
                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                    <small class="text-muted password-requirements">
                        Password must be at least 5 characters long and contain at least one number
                    </small>
                    <div id="passwordRequirements" class="text-danger password-requirements"></div>
                </div>
                
                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Confirm Password</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                </div>
                
                <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary">Register</button>
                </div>
                
                <div class="text-center mt-3">
                    <p class="text-muted">
                        Already have an account? 
                        <a href="login.jsp" class="text-decoration-none">Login here</a>
                    </p>
                </div>
            </form>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            // Έλεγχος μήκους
            if (password.length < 5) {
                e.preventDefault();
                alert('Password must be at least 5 characters long');
                return;
            }
            
            // Έλεγχος για αριθμό
            if (!/\d/.test(password)) {
                e.preventDefault();
                alert('Password must contain at least one number');
                return;
            }
            
            // Έλεγχος ταιριάσματος κωδικών
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Passwords do not match!');
                return;
            }
        });

        // Real-time password validation
        document.getElementById('password').addEventListener('input', function() {
            const password = this.value;
            let message = '';
            
            if (password.length < 5) {
                message += 'Password must be at least 5 characters long\n';
            }
            if (!/\d/.test(password)) {
                message += 'Password must contain at least one number\n';
            }
            
            // Update password requirements message
            document.getElementById('passwordRequirements').textContent = message;
        });
    </script>
</body>
</html>