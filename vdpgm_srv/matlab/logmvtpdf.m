function logpdf = logmvtpdf(t,mu,f,Sigma)
% function logpdf = logmvtpdf(t,mu,f,Sigma);
% log pdf of multivariate t-student dist.
% t : D by N

[d,n] = size(t);

c = gammaln((d+f)*0.5) - (d*0.5)*log(f*pi) - gammaln(f*0.5) - 0.5*detln(Sigma);
diff = t - repmat(mu,1,n); % d by n
logpdf = c - (f+d)*0.5 * log(1 + sum(diff.*((f*Sigma)\diff),1));

% Local Variables: ***
% mode: matlab ***
% End: ***

function [Y] = detln( X )
% Y = logdet( X )
% return a log determinant of X
[d, err] = chol(X);
if err
  error('error in Choleski disposition for detln');
end
Y = sum(log(diag(d))) *2;
