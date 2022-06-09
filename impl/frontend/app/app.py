from flask import Flask, render_template, send_from_directory, url_for, request, redirect
from flask_login import LoginManager, login_manager, current_user, login_user, login_required, logout_user
import requests
import json
import os

# Usuarios
from models import users, User

# Login
from forms import LoginForm, SignupForm, SendVideoForm

# Send Video
from forms import SendVideoForm

app = Flask(__name__, static_url_path='')
login_manager = LoginManager()
login_manager.init_app(app) # Para mantener la sesión

# Configurar el secret_key. OJO, no debe ir en un servidor git público.
# Python ofrece varias formas de almacenar esto de forma segura, que
# no cubriremos aquí.
app.config['SECRET_KEY'] = 'qH1vprMjavek52cv7Lmfe1FoCexrrV8egFnB21jHhkuOHm8hJUe1hwn7pKEZQ1fioUzDb3sWcNK1pJVVIhyrgvFiIrceXpKJBFIn_i9-LTLBCc4cqaI3gjJJHU6kxuT8bnC7Ng'

@app.route('/static/<path:path>')
def serve_static(path):
    return send_from_directory('static', path)

# Icon!
@app.route('/favicon.ico')
def favicon():
    return send_from_directory('static', 'favicon.ico')

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if current_user.is_authenticated:
        return redirect(url_for('index'))
    else:
        error = None
        form = LoginForm(request.form)
        if request.method == "POST" and  form.validate():
            user = {"email": form.email.data, "password": form.password.data}
            #BACKEND_REST = os.environ.get('BACKEND_REST', 'localhost')
            #print(form.email.data)
            response = requests.post("http://"+os.environ['BACKEND_REST']+":8080/rest/checkLogin", data=user, headers={"Content-Type": "application/json"})
            #print("HOLA HE PRINTEADO")

            if response.status_code != 200:
                error = 'Invalid Credentials. Please try again'
            else:
                campos = response.json()
                #print(campos)
                userValidado = User(campos[u'id'], campos[u'name'], campos[u'email'], campos[u'password'], campos[u'token'], campos['visits'])
                users.append()
                login_user(userValidado, remember=form.remember_me.data)
                return redirect(url_for('profile'))
        return render_template('login.html', form=form,  error=error)
        
@app.route('/signup', methods=['GET', 'POST'])
def signup():
    if current_user.is_authenticated:
        return redirect(url_for('index'))
    else:
        error = None
        form = SignupForm(request.form)
        if request.method == "POST":
            user = {"email":form.email.data, "password": form.password.data, "name": form.name.data}
            header = {"Content-Type": "application/json"}
            BACKEND_REST = os.environ.get('BACKEND_REST', 'localhost')
            response = requests.post('http://'+BACKEND_REST+':8080/users', data=json.dumps(user), headers=header)
            if response.status_code != 200:
                error='Invalid User. Please, try again'
            else:    
                return redirect(url_for('index'))
        return render_template('signup.html', form=form,  error=error)
        

@app.route('/send_video', methods=['GET', 'POST'])
@login_required
def send_video():
    error = None
    form = SendVideoForm()
    if request.method == "POST" and form.validate():
        # A video is being sent
        files = {'file': (form.file.data.filename, form.file.data)} # file stream to resend
        # print(requests.Request('POST', 'http://localhost:8080/rest/uploadVideo',
        #                        files=files).prepare().body.decode('utf-8'))
        REST_SERVER = os.environ.get('REST_SERVER', 'localhost')
        response = requests.post('http://'+REST_SERVER+':8080/Service/uploadVideo', files=files)
        if response.status_code == 200:
            error = "Video uploaded successfully"
        else:
            error = response.text
    return render_template('send_video.html', form=form, error=error)


@app.route('/profile')
@login_required
def profile():
    return render_template('profile.html')

@app.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect(url_for('index'))

@login_manager.user_loader
def load_user(user_id):
    for user in users:
        if user.id == int(user_id):
            return user
    return None

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
